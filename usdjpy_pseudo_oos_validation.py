"""
USDJPY PSEUDO-OOS VALIDATION
Using 2020-2022 as in-sample, 2023-2026 as pseudo-OOS
Run from: C:\data\dukascopy\out\
"""

import pandas as pd
import numpy as np
from pathlib import Path
import warnings
warnings.filterwarnings('ignore')

# ============================================================================
# STEP 1: LOAD AND COMBINE ALL BID DATA
# ============================================================================

DATA_DIR = Path("C:/data/dukascopy/out")

bid_files = [
    "USDJPY_M1_2020-2021.csv",
    "USDJPY_M1_2021-2022.csv",
    "USDJPY_M1_2022-2023.csv",
    "USDJPY_M1_2023-2024.csv",
    "USDJPY_M1_2024-2025.csv",
    "USDJPY_M1_2025-2026.csv",
]

ask_files = [
    "USDJPY_M1_2020-2021_ask.csv",
    "USDJPY_M1_2021-2022_ask.csv",
    "USDJPY_M1_2022-2023_ask.csv",
    "USDJPY_M1_2023-2024_ask.csv",
    "USDJPY_M1_2024-2025_ask.csv",
    "USDJPY_M1_2025-2026_ask.csv",
]

print("Loading data...")

# Load bid data
bid_dfs = []
for f in bid_files:
    filepath = DATA_DIR / f
    if filepath.exists():
        df = pd.read_csv(filepath)
        bid_dfs.append(df)
        print(f"  Loaded {f}: {len(df):,} rows")
    else:
        print(f"  Missing: {f}")

bid_data = pd.concat(bid_dfs, ignore_index=True)

# Load ask data
ask_dfs = []
for f in ask_files:
    filepath = DATA_DIR / f
    if filepath.exists():
        df = pd.read_csv(filepath)
        ask_dfs.append(df)
ask_data = pd.concat(ask_dfs, ignore_index=True)

print(f"\nTotal rows: {len(bid_data):,}")
print(f"Columns: {list(bid_data.columns)}")
print(f"\nFirst few rows:")
print(bid_data.head(3))

# ============================================================================
# STEP 2: NORMALIZE COLUMNS
# ============================================================================

# Print actual column names first so we can map them
print(f"\nBid columns: {list(bid_data.columns)}")
print(f"Ask columns: {list(ask_data.columns)}")

# Dukascopy typical format: Timestamp, Open, High, Low, Close, Volume
# Adjust these mappings based on what you see above
col_map = {
    bid_data.columns[0]: 'time',
    bid_data.columns[1]: 'open',
    bid_data.columns[2]: 'high',
    bid_data.columns[3]: 'low',
    bid_data.columns[4]: 'close',
}
if len(bid_data.columns) > 5:
    col_map[bid_data.columns[5]] = 'volume'

bid_data = bid_data.rename(columns=col_map)
bid_data['time'] = pd.to_datetime(bid_data['time'])
bid_data = bid_data.sort_values('time').reset_index(drop=True)

# Add ask close for spread calculation
ask_col_map = {ask_data.columns[0]: 'time', ask_data.columns[4]: 'ask_close'}
ask_data = ask_data.rename(columns=ask_col_map)
ask_data['time'] = pd.to_datetime(ask_data['time'])

# Merge bid and ask
data = bid_data.merge(ask_data[['time', 'ask_close']], on='time', how='left')
data['spread_pips'] = (data['ask_close'] - data['close']) * 100

print(f"\nDate range: {data['time'].min()} to {data['time'].max()}")
print(f"Average spread: {data['spread_pips'].mean():.2f} pips")

# ============================================================================
# STEP 3: AGGREGATE M1 -> M5
# ============================================================================

print("\nAggregating M1 -> M5...")

data = data.set_index('time')

# Resample to M5 (5-minute bars)
m5 = data['close'].resample('5min').ohlc()
m5.columns = ['open', 'high', 'low', 'close']
m5['volume'] = data['volume'].resample('5min').sum() if 'volume' in data.columns else 1
m5['spread_pips'] = data['spread_pips'].resample('5min').mean()
m5 = m5.dropna()

print(f"M5 bars: {len(m5):,}")
print(f"Date range: {m5.index.min()} to {m5.index.max()}")

# ============================================================================
# STEP 4: CALCULATE INDICATORS
# ============================================================================

print("\nCalculating indicators...")

def calculate_atr(df, period=14):
    high = df['high']
    low = df['low']
    close = df['close']
    tr1 = high - low
    tr2 = abs(high - close.shift(1))
    tr3 = abs(low - close.shift(1))
    tr = pd.concat([tr1, tr2, tr3], axis=1).max(axis=1)
    return tr.ewm(span=period, adjust=False).mean()

m5['atr'] = calculate_atr(m5)
m5['atr_pips'] = m5['atr'] * 100
m5['pip_size'] = 0.01  # USDJPY

# Rolling momentum: price change over last N bars
MOMENTUM_BARS = 3
m5['momentum'] = m5['close'].diff(MOMENTUM_BARS) * 100  # in pips

# EMA for context
m5['ema_20'] = m5['close'].ewm(span=20, adjust=False).mean()

print(f"ATR average: {m5['atr_pips'].mean():.1f} pips")
print(f"Momentum std: {m5['momentum'].std():.1f} pips")

# ============================================================================
# STEP 5: STRATEGY - MOMENTUM WITH 10-HOUR TIME EXIT
# ============================================================================

def run_backtest(df,
                 momentum_threshold=20,
                 hold_bars=120,  # 10 hours x 12 M5 bars/hour
                 stop_pips=672,
                 tp_pips=1344,
                 initial_capital=200,
                 risk_pct=0.15,
                 label=""):
    """
    10-hour momentum time exit strategy

    Entry: Momentum > threshold pips (buy) or < -threshold (sell)
    Exit: After exactly hold_bars (10 hours by default)
    SL/TP: Wide safety nets only
    """

    print(f"\n{'='*60}")
    print(f"BACKTEST: {label}")
    print(f"{'='*60}")
    print(f"Bars: {len(df):,}")
    print(f"Period: {df.index.min()} to {df.index.max()}")
    print(f"Momentum threshold: {momentum_threshold} pips")
    print(f"Hold period: {hold_bars} bars ({hold_bars/12:.0f} hours)")

    trades = []
    capital = initial_capital
    in_trade = False
    trade_entry_idx = None

    for i in range(MOMENTUM_BARS + 20, len(df) - hold_bars):

        if in_trade:
            # Check if exit time reached
            bars_held = i - trade_entry_idx
            if bars_held >= hold_bars:
                # Time exit
                exit_price = df.iloc[i]['close']
                entry_price = current_trade['entry_price']

                if current_trade['direction'] == 1:
                    pips = (exit_price - entry_price) * 100
                else:
                    pips = (entry_price - exit_price) * 100

                # Apply spread cost
                spread = df.iloc[i]['spread_pips'] if 'spread_pips' in df.columns else 1.5
                pips_net = pips - spread

                # Dollar return
                dollar_return = pips_net * lot_size * (1/100)  # USDJPY pip value approx

                capital_before = current_trade['capital_before']
                capital += dollar_return

                trades.append({
                    'entry_time': current_trade['entry_time'],
                    'exit_time': df.index[i],
                    'entry_price': entry_price,
                    'exit_price': exit_price,
                    'direction': current_trade['direction'],
                    'pips_gross': pips,
                    'pips_net': pips_net,
                    'lot_size': lot_size,
                    'dollar_return': dollar_return,
                    'return_pct': dollar_return / capital_before,
                    'capital_before': capital_before,
                    'capital_after': capital,
                    'exit_reason': 'TIME',
                    'is_win': pips_net > 0
                })

                in_trade = False
            continue

        # Entry logic
        row = df.iloc[i]
        momentum = row['momentum']

        # Skip Tokyo session (00:00 - 08:00 UTC)
        hour = df.index[i].hour
        if 0 <= hour < 8:
            continue

        # Check momentum signal
        if abs(momentum) < momentum_threshold:
            continue

        direction = 1 if momentum > 0 else -1
        entry_price = row['close']

        # Position sizing based on risk %
        atr_pips = row['atr_pips']
        risk_amount = capital * risk_pct
        lot_size = max(0.01, round(risk_amount / (40 * 10), 2))  # 40 pip expected move

        in_trade = True
        trade_entry_idx = i
        current_trade = {
            'entry_time': df.index[i],
            'entry_price': entry_price,
            'direction': direction,
            'capital_before': capital,
        }
        lot_size = lot_size  # captured in closure

    if len(trades) == 0:
        print("WARNING: NO TRADES GENERATED - Check momentum threshold")
        return None, None

    results = pd.DataFrame(trades)

    # Statistics
    win_rate = results['is_win'].mean()
    total_return = (capital / initial_capital - 1) * 100
    avg_win = results[results['is_win']]['pips_net'].mean()
    avg_loss = results[~results['is_win']]['pips_net'].mean()

    # Max drawdown
    results['cummax'] = results['capital_after'].cummax()
    results['drawdown'] = (results['capital_after'] - results['cummax']) / results['cummax'] * 100
    max_dd = results['drawdown'].min()

    # Sharpe
    if results['return_pct'].std() > 0:
        sharpe = (results['return_pct'].mean() / results['return_pct'].std()) * np.sqrt(252)
    else:
        sharpe = 0

    stats = {
        'trades': len(results),
        'win_rate': win_rate,
        'total_return_pct': total_return,
        'final_capital': capital,
        'avg_win_pips': avg_win,
        'avg_loss_pips': avg_loss,
        'max_drawdown_pct': max_dd,
        'sharpe': sharpe,
        'profit_factor': abs(results[results['is_win']]['pips_net'].sum() / results[~results['is_win']]['pips_net'].sum()) if results[~results['is_win']]['pips_net'].sum() != 0 else 999
    }

    print(f"\nRESULTS:")
    print(f"  Trades:       {stats['trades']}")
    print(f"  Win Rate:     {stats['win_rate']*100:.1f}%")
    print(f"  Total Return: {stats['total_return_pct']:.1f}%")
    print(f"  Final Capital: ${stats['final_capital']:.2f}")
    print(f"  Avg Win:      {stats['avg_win_pips']:.1f} pips")
    print(f"  Avg Loss:     {stats['avg_loss_pips']:.1f} pips")
    print(f"  Max Drawdown: {stats['max_drawdown_pct']:.2f}%")
    print(f"  Sharpe:       {stats['sharpe']:.2f}")
    print(f"  Profit Factor:{stats['profit_factor']:.2f}")

    return results, stats

# ============================================================================
# STEP 6: SPLIT DATA AND RUN OOS TEST
# ============================================================================

# IN-SAMPLE: 2020-2022 (first part of your data)
in_sample = m5['2020-01-01':'2022-12-31']

# PSEUDO-OOS: 2023-2026 (never used for optimization)
oos = m5['2023-01-01':]

print("\n" + "="*60)
print("DATA SPLIT:")
print("="*60)
print(f"In-sample:   {in_sample.index.min()} -> {in_sample.index.max()} ({len(in_sample):,} bars)")
print(f"Pseudo-OOS:  {oos.index.min()} -> {oos.index.max()} ({len(oos):,} bars)")

# Run both
trades_is, stats_is = run_backtest(
    in_sample,
    momentum_threshold=20,
    hold_bars=120,
    initial_capital=200,
    risk_pct=0.15,
    label="IN-SAMPLE (2020-2022)"
)

trades_oos, stats_oos = run_backtest(
    oos,
    momentum_threshold=20,  # SAME PARAMETERS - no re-optimization
    hold_bars=120,
    initial_capital=200,
    risk_pct=0.15,
    label="PSEUDO-OOS (2023-2026)"
)

# ============================================================================
# STEP 7: THE VERDICT
# ============================================================================

print("\n" + "="*80)
print("VALIDATION VERDICT")
print("="*80)

if stats_is and stats_oos:
    wr_is = stats_is['win_rate'] * 100
    wr_oos = stats_oos['win_rate'] * 100
    degradation = wr_is - wr_oos

    print(f"\n{'Metric':<25} {'In-Sample':>15} {'Pseudo-OOS':>15} {'Change':>10}")
    print("-"*65)
    print(f"{'Win Rate':<25} {wr_is:>14.1f}% {wr_oos:>14.1f}% {-degradation:>+9.1f}%")
    print(f"{'Total Return':<25} {stats_is['total_return_pct']:>14.1f}% {stats_oos['total_return_pct']:>14.1f}%")
    print(f"{'Sharpe':<25} {stats_is['sharpe']:>15.2f} {stats_oos['sharpe']:>15.2f}")
    print(f"{'Max Drawdown':<25} {stats_is['max_drawdown_pct']:>14.2f}% {stats_oos['max_drawdown_pct']:>14.2f}%")
    print(f"{'Profit Factor':<25} {stats_is['profit_factor']:>15.2f} {stats_oos['profit_factor']:>15.2f}")
    print(f"{'Trades':<25} {stats_is['trades']:>15} {stats_oos['trades']:>15}")

    print("\n" + "="*80)
    print("VERDICT:")
    print("="*80)

    if wr_oos >= 70 and stats_oos['total_return_pct'] > 0:
        print(f"\nSTRONG PASS: {wr_oos:.1f}% OOS win rate >= 70%")
        print("You CAN confidently claim 70%+ directional accuracy")
        print("Strategy shows real edge on unseen data")
    elif wr_oos >= 60 and stats_oos['total_return_pct'] > 0:
        print(f"\nWEAK PASS: {wr_oos:.1f}% OOS win rate (60-70%)")
        print("Strategy has SOME edge but don't claim 70%")
        print("Trade cautiously with smaller position sizes")
    elif wr_oos >= 55 and stats_oos['total_return_pct'] > 0:
        print(f"\nMARGINAL: {wr_oos:.1f}% OOS win rate (55-60%)")
        print("Edge is weak - strategy may not survive live conditions")
        print("Do NOT claim 70% directional accuracy")
    else:
        print(f"\nFAILED: {wr_oos:.1f}% OOS win rate < 55%")
        print("Strategy does NOT work on unseen data")
        print("This is overfit to your training period")
        print("DO NOT TRADE THIS LIVE")

    print("\n" + "="*80)
    print("CRITICAL REMINDER:")
    print("="*80)
    print("This is PSEUDO-OOS (2023-2026), not TRUE OOS (2015-2019)")
    print("Get Dukascopy 2015-2019 data for definitive validation")
    print("="*80)

# Save results
output_dir = Path("C:/Users/kingc/Project Titan/validation_results")
output_dir.mkdir(exist_ok=True)

if trades_is is not None:
    trades_is.to_csv(output_dir / "insample_trades.csv", index=False)
if trades_oos is not None:
    trades_oos.to_csv(output_dir / "oos_trades.csv", index=False)

print(f"\nResults saved to: {output_dir}")
