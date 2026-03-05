import pandas as pd
import mysql.connector

excel_path = 'c:/Users/86152/Desktop/NBA/site/assets/Basic.xlsx'

df = pd.read_excel(excel_path)
print('列名:', df.columns.tolist())
print('行数:', len(df))
print('前5行:')
print(df.head())

conn = mysql.connector.connect(
    host='localhost',
    user='root',
    password='MZS3707252004',
    database='nbademo',
    charset='utf8mb4'
)
cursor = conn.cursor()

cursor.execute('TRUNCATE TABLE nbaplayerbasicstatistics')
print('已清空旧数据')

for idx, row in df.iterrows():
    player_rank = int(row['PlayerRank']) if pd.notna(row.get('PlayerRank')) else idx + 1
    player_image = str(row['PlayerImage']) if pd.notna(row.get('PlayerImage')) else ''
    surname = str(row['Surname']) if pd.notna(row.get('Surname')) else ''
    name = str(row['Name']) if pd.notna(row.get('Name')) else ''
    team = str(row['Team']) if pd.notna(row.get('Team')) else ''
    number_of_matches = int(row['NumberOfMatches']) if pd.notna(row.get('NumberOfMatches')) else 0
    time = float(row['Time']) if pd.notna(row.get('Time')) else 0.0
    score = float(row['Score']) if pd.notna(row.get('Score')) else 0.0
    rebound = float(row['Rebound']) if pd.notna(row.get('Rebound')) else 0.0
    assist = float(row['Assist']) if pd.notna(row.get('Assist')) else 0.0
    steal = float(row['Steal']) if pd.notna(row.get('Steal')) else 0.0
    block = float(row['Block']) if pd.notna(row.get('Block')) else 0.0
    field_goal_per = str(row['FieldGoalPer']) if pd.notna(row.get('FieldGoalPer')) else ''
    three_point_field_goals = int(row['ThreePointFieldGoals']) if pd.notna(row.get('ThreePointFieldGoals')) else 0
    three_point_shooting_per = str(row['ThreePointShootingPer']) if pd.notna(row.get('ThreePointShootingPer')) else ''
    free_throw_shooting_per = str(row['FreeThrowShootingPer']) if pd.notna(row.get('FreeThrowShootingPer')) else ''
    
    sql = '''
    INSERT INTO nbaplayerbasicstatistics 
    (PlayerRank, PlayerImage, Surname, Name, Team, NumberOfMatches, Time, Score, Rebound, Assist, Steal, Block, FieldGoalPer, ThreePointFieldGoals, ThreePointShootingPer, FreeThrowShootingPer, entry_time, update_time)
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW())
    '''
    values = (player_rank, player_image, surname, name, team, number_of_matches, time, score, rebound, assist, steal, block, field_goal_per, three_point_field_goals, three_point_shooting_per, free_throw_shooting_per)
    cursor.execute(sql, values)

conn.commit()
print(f'成功插入 {len(df)} 条数据')

cursor.execute('SELECT PlayerRank, Surname, Name, Team, Score FROM nbaplayerbasicstatistics ORDER BY PlayerRank LIMIT 10')
print('\n验证数据:')
for row in cursor.fetchall():
    print(f'{row[0]}. {row[1]}{row[2]} - {row[3]}: {row[4]}分')

cursor.close()
conn.close()
