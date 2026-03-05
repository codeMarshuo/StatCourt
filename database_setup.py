import mysql.connector
from mysql.connector import Error
import json

def get_mysql_connection(host='localhost', user='root', password='', database=None):
    """创建MySQL数据库连接"""
    try:
        connection = mysql.connector.connect(
            host=host,
            user=user,
            password=password,
            database=database
        )
        return connection
    except Error as e:
        print(f"连接MySQL数据库失败: {e}")
        return None

def show_databases(connection):
    """显示所有数据库"""
    if connection is None:
        return []
    
    try:
        cursor = connection.cursor()
        cursor.execute("SHOW DATABASES")
        databases = [db[0] for db in cursor.fetchall()]
        cursor.close()
        return databases
    except Error as e:
        print(f"获取数据库列表失败: {e}")
        return []

def show_tables(connection, database_name):
    """显示指定数据库中的所有表"""
    if connection is None:
        return []
    
    try:
        cursor = connection.cursor()
        cursor.execute(f"USE {database_name}")
        cursor.execute("SHOW TABLES")
        tables = [table[0] for table in cursor.fetchall()]
        cursor.close()
        return tables
    except Error as e:
        print(f"获取表列表失败: {e}")
        return []

def get_table_data(connection, database_name, table_name, limit=50):
    """获取表中的数据"""
    if connection is None:
        return []
    
    try:
        cursor = connection.cursor(dictionary=True)
        cursor.execute(f"USE {database_name}")
        cursor.execute(f"SELECT * FROM {table_name} LIMIT {limit}")
        data = cursor.fetchall()
        cursor.close()
        return data
    except Error as e:
        print(f"获取表数据失败: {e}")
        return []

def create_nba_database(connection):
    """创建NBA数据库和表"""
    if connection is None:
        return False
    
    try:
        cursor = connection.cursor()
        
        # 创建数据库
        cursor.execute("CREATE DATABASE IF NOT EXISTS nba_stats DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
        cursor.execute("USE nba_stats")
        
        # 删除已存在的表（按依赖顺序）
        cursor.execute("DROP TABLE IF EXISTS t_player_season_stats")
        cursor.execute("DROP TABLE IF EXISTS t_player")
        cursor.execute("DROP TABLE IF EXISTS t_team")
        cursor.execute("DROP TABLE IF EXISTS t_season")
        
        # 创建赛季表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS t_season (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                season_year VARCHAR(20) NOT NULL,
                season_type TINYINT NOT NULL COMMENT '1-常规赛，2-季后赛',
                season_name VARCHAR(50) NOT NULL,
                start_time DATE,
                end_time DATE,
                status TINYINT NOT NULL DEFAULT 1 COMMENT '0-未开始，1-进行中，2-已结束',
                create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                is_deleted TINYINT NOT NULL DEFAULT 0,
                UNIQUE KEY uk_season_year_type (season_year, season_type, is_deleted)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """)
        
        # 创建球队表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS t_team (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                team_code VARCHAR(30) NOT NULL,
                team_name VARCHAR(50) NOT NULL,
                team_short_name VARCHAR(20) NOT NULL,
                city VARCHAR(50),
                conference VARCHAR(20),
                division VARCHAR(20),
                logo_url VARCHAR(255),
                found_year VARCHAR(10),
                status TINYINT NOT NULL DEFAULT 1,
                create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                is_deleted TINYINT NOT NULL DEFAULT 0,
                UNIQUE KEY uk_team_code (team_code, is_deleted)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """)
        
        # 创建球员基础信息表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS t_player (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                player_id BIGINT NOT NULL,
                chinese_name VARCHAR(50) NOT NULL,
                english_name VARCHAR(100) NOT NULL,
                country VARCHAR(50),
                birth_date DATE,
                height DECIMAL(5,2),
                weight DECIMAL(5,2),
                position VARCHAR(30),
                jersey_number VARCHAR(10),
                status TINYINT NOT NULL DEFAULT 1 COMMENT '0-退役，1-现役',
                create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                is_deleted TINYINT NOT NULL DEFAULT 0,
                UNIQUE KEY uk_player_id (player_id, is_deleted),
                KEY idx_chinese_name (chinese_name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """)
        
        # 创建球员赛季数据表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS t_player_season_stats (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                season_id BIGINT NOT NULL,
                player_id BIGINT NOT NULL,
                team_id BIGINT NOT NULL,
                `rank` INT,
                games INT NOT NULL DEFAULT 0,
                minutes_per_game DECIMAL(4,1),
                points_per_game DECIMAL(4,1) NOT NULL DEFAULT 0.0,
                rebounds_per_game DECIMAL(4,1) NOT NULL DEFAULT 0.0,
                assists_per_game DECIMAL(4,1) NOT NULL DEFAULT 0.0,
                steals_per_game DECIMAL(3,1) NOT NULL DEFAULT 0.0,
                blocks_per_game DECIMAL(3,1) NOT NULL DEFAULT 0.0,
                field_goal_percentage DECIMAL(4,1),
                three_pointers_made INT NOT NULL DEFAULT 0,
                three_point_percentage DECIMAL(4,1),
                free_throw_percentage DECIMAL(4,1),
                data_update_time DATETIME,
                create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                is_deleted TINYINT NOT NULL DEFAULT 0,
                UNIQUE KEY uk_season_player_team (season_id, player_id, team_id, is_deleted),
                KEY idx_season_id (season_id),
                KEY idx_team_id (team_id),
                KEY idx_points_rank (season_id, `rank`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """)
        
        # 插入示例数据 - 赛季
        cursor.execute("""
            INSERT IGNORE INTO t_season (season_year, season_type, season_name, status) VALUES
            ('2025-2026', 1, '2025-2026赛季常规赛', 1),
            ('2025-2026', 2, '2025-2026赛季季后赛', 1)
        """)
        
        # 插入示例数据 - 球队
        cursor.execute("""
            INSERT IGNORE INTO t_team (team_code, team_name, team_short_name, city, conference, division, status) VALUES
            ('BOS', '波士顿凯尔特人', '凯尔特人', '波士顿', '东部', '大西洋', 1),
            ('DEN', '丹佛掘金', '掘金', '丹佛', '西部', '西北', 1),
            ('OKC', '俄克拉荷马雷霆', '雷霆', '俄克拉荷马', '西部', '西北', 1),
            ('MIL', '密尔沃基雄鹿', '雄鹿', '密尔沃基', '东部', '中央', 1),
            ('LAC', '洛杉矶快船', '快船', '洛杉矶', '西部', '太平洋', 1)
        """)
        
        # 插入示例数据 - 球员
        cursor.execute("""
            INSERT IGNORE INTO t_player (player_id, chinese_name, english_name, country, position, jersey_number, status) VALUES
            (1, '杰森·塔图姆', 'Jayson Tatum', '美国', '小前锋', '0', 1),
            (2, '尼古拉·约基奇', 'Nikola Jokic', '塞尔维亚', '中锋', '15', 1),
            (3, '卢卡·东契奇', 'Luka Doncic', '斯洛文尼亚', '控球后卫', '77', 1),
            (4, '扬尼斯·阿德托昆博', 'Giannis Antetokounmpo', '希腊', '大前锋', '34', 1),
            (5, '科怀·伦纳德', 'Kawhi Leonard', '美国', '小前锋', '2', 1)
        """)
        
        # 插入示例数据 - 球员赛季数据
        cursor.execute("""
            INSERT IGNORE INTO t_player_season_stats 
            (season_id, player_id, team_id, `rank`, games, minutes_per_game, points_per_game, 
             rebounds_per_game, assists_per_game, steals_per_game, blocks_per_game,
             field_goal_percentage, three_point_percentage, free_throw_percentage) VALUES
            (1, 1, 1, 1, 72, 36.5, 30.1, 8.2, 4.9, 1.2, 0.8, 47.5, 38.2, 85.3),
            (1, 2, 2, 2, 70, 34.2, 28.5, 12.8, 9.5, 1.4, 0.7, 56.8, 34.5, 81.2),
            (1, 3, 3, 3, 71, 37.1, 29.8, 8.5, 8.9, 1.1, 0.5, 48.2, 36.8, 78.6),
            (1, 4, 4, 4, 68, 35.8, 27.9, 11.3, 5.2, 1.3, 1.1, 55.3, 31.2, 70.5),
            (1, 5, 5, 5, 65, 33.5, 26.2, 6.1, 3.8, 1.6, 0.6, 50.1, 40.3, 87.9)
        """)
        
        connection.commit()
        cursor.close()
        print("NBA数据库和表创建成功！已插入示例数据。")
        return True
    except Error as e:
        print(f"创建数据库失败: {e}")
        return False

def export_data_to_json(connection, database_name, output_file):
    """导出数据库数据到JSON文件"""
    if connection is None:
        return False
    
    try:
        tables = show_tables(connection, database_name)
        data = {}
        
        for table in tables:
            table_data = get_table_data(connection, database_name, table)
            data[table] = table_data
        
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2, default=str)
        
        print(f"数据已成功导出到 {output_file}")
        return True
    except Exception as e:
        print(f"导出数据失败: {e}")
        return False

if __name__ == "__main__":
    print("=== NBA数据库设置脚本 ===")
    
    # 尝试连接MySQL
    print("\n尝试连接MySQL数据库...")
    conn = get_mysql_connection(password='MZS3707252004')
    
    if conn:
        print("✓ 成功连接到MySQL数据库")
        
        # 显示数据库列表
        databases = show_databases(conn)
        print(f"\n现有数据库: {databases}")
        
        # 检查是否已存在nba_stats数据库
        print("\n开始创建NBA数据库和表...")
        create_nba_database(conn)
        
        # 显示表列表
        tables = show_tables(conn, 'nba_stats')
        print(f"\n数据库中的表: {tables}")
        
        # 导出数据
        export_data_to_json(conn, 'nba_stats', 'c:\\Users\\86152\\Desktop\\NBA\\site\\data.json')
        
        conn.close()
    else:
        print("\n请提供正确的MySQL密码来连接数据库！")
