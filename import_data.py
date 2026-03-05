import pandas as pd
import pymysql
from sqlalchemy import create_engine

# 读取Excel文件
df = pd.read_excel('c:/Users/86152/Desktop/NBA/site/assets/NBAadvancedstats.csv')

# 重命名列名以匹配数据库字段
df.columns = ['rk', 'player', 'age', 'team', 'pos', 'g', 'gs', 'mp', 
              'per', 'ts_pct', 'three_par', 'ftr', 
              'orb_pct', 'drb_pct', 'trb_pct', 'ast_pct', 'stl_pct', 'blk_pct', 
              'tov_pct', 'usg_pct', 'ows', 'dws', 'ws', 'ws_48', 
              'obpm', 'dbpm', 'bpm', 'vorp', 'awards', 'player_additional']

# 处理NaN值
df = df.where(pd.notnull(df), None)

# 连接数据库
engine = create_engine('mysql+pymysql://root:MZS3707252004@localhost:3306/nbademo?charset=utf8mb4')

# 导入数据
df.to_sql('nba_advanced_stats', con=engine, if_exists='append', index=False)

print(f'成功导入 {len(df)} 条数据到数据库')
