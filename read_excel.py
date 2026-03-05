import pandas as pd
import json

# 读取Excel文件
df = pd.read_excel('c:/Users/86152/Desktop/NBA/site/assets/NBAadvancedstats.csv')

# 显示前5行数据
print('前5行数据:')
print(df.head().to_string())
print('\n列名:', df.columns.tolist())
print('\n数据类型:')
print(df.dtypes)
print('\n总行数:', len(df))
