#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
篮球数据收集模块
用于收集NBA球队和球员数据
"""

import json
import pandas as pd
import numpy as np
from typing import Dict, List, Optional
from datetime import datetime
import requests


class BasketballDataCollector:
    """篮球数据收集器"""
    
    def __init__(self, api_base_url: str = "http://localhost:8081"):
        self.api_base_url = api_base_url
        self.teams_data = []
        self.players_data = []
    
    def fetch_teams_from_api(self) -> List[Dict]:
        """从本地API获取球队数据"""
        try:
            response = requests.get(f"{self.api_base_url}/api/data")
            if response.status_code == 200:
                data = response.json()
                if data.get('code') in [200, 0]:
                    self.teams_data = data.get('data', {}).get('teams', [])
                    return self.teams_data
        except Exception as e:
            print(f"获取球队数据失败: {e}")
        return []
    
    def fetch_players_from_api(self) -> List[Dict]:
        """从本地API获取球员数据"""
        try:
            response = requests.get(f"{self.api_base_url}/api/data")
            if response.status_code == 200:
                data = response.json()
                if data.get('code') in [200, 0]:
                    self.players_data = data.get('data', {}).get('players', [])
                    return self.players_data
        except Exception as e:
            print(f"获取球员数据失败: {e}")
        return []
    
    def load_teams_from_json(self, filepath: str) -> List[Dict]:
        """从JSON文件加载球队数据"""
        try:
            with open(filepath, 'r', encoding='utf-8') as f:
                self.teams_data = json.load(f)
                return self.teams_data
        except Exception as e:
            print(f"加载球队数据失败: {e}")
        return []
    
    def load_players_from_json(self, filepath: str) -> List[Dict]:
        """从JSON文件加载球员数据"""
        try:
            with open(filepath, 'r', encoding='utf-8') as f:
                self.players_data = json.load(f)
                return self.players_data
        except Exception as e:
            print(f"加载球员数据失败: {e}")
        return []
    
    def process_team_stats(self, team: Dict) -> Dict:
        """处理球队统计数据"""
        processed = {
            'team_id': team.get('teamid'),
            'team_name': team.get('teamname'),
            'team_logo': team.get('teamlogo'),
            'games': team.get('games', 0),
            'wins': team.get('wins', 0),
            'losses': team.get('losses', 0),
            'winrate': team.get('winrate', 0),
            'ppg': team.get('ppg', 0),
            'oppg': team.get('oppg', 0),
            'pointdiff': team.get('pointdiff', 0),
            'off_rating': team.get('offRating', 110),
            'def_rating': team.get('defRating', 110),
            'pace': team.get('pace', 100),
            'rank': team.get('teamrank', 0)
        }
        
        processed['net_rating'] = processed['off_rating'] - processed['def_rating']
        
        return processed
    
    def process_all_teams(self) -> List[Dict]:
        """处理所有球队数据"""
        return [self.process_team_stats(team) for team in self.teams_data]
    
    def calculate_league_averages(self) -> Dict:
        """计算联盟平均数据"""
        if not self.teams_data:
            return {}
        
        df = pd.DataFrame(self.teams_data)
        
        return {
            'avg_ppg': df['ppg'].mean() if 'ppg' in df else 110,
            'avg_oppg': df['oppg'].mean() if 'oppg' in df else 110,
            'avg_pace': df['pace'].mean() if 'pace' in df else 100,
            'avg_off_rating': df['offRating'].mean() if 'offRating' in df else 110,
            'avg_def_rating': df['defRating'].mean() if 'defRating' in df else 110,
            'avg_winrate': df['winrate'].mean() if 'winrate' in df else 0.5
        }
    
    def get_team_by_name(self, team_name: str) -> Optional[Dict]:
        """根据名称获取球队"""
        for team in self.teams_data:
            if team_name in team.get('teamname', ''):
                return team
        return None
    
    def save_to_json(self, data: List[Dict], filepath: str):
        """保存数据到JSON文件"""
        try:
            with open(filepath, 'w', encoding='utf-8') as f:
                json.dump(data, f, ensure_ascii=False, indent=2)
            print(f"数据已保存到: {filepath}")
        except Exception as e:
            print(f"保存数据失败: {e}")
    
    def generate_match_data(self, home_team_name: str, away_team_name: str,
                           home_odds: Optional[float] = None,
                           away_odds: Optional[float] = None,
                           handicap: Optional[float] = None,
                           total_line: Optional[float] = None) -> Dict:
        """生成比赛数据"""
        home_team = self.get_team_by_name(home_team_name)
        away_team = self.get_team_by_name(away_team_name)
        
        if not home_team or not away_team:
            return {'error': '球队不存在'}
        
        return {
            'home_team': self.process_team_stats(home_team),
            'away_team': self.process_team_stats(away_team),
            'home_odds': home_odds,
            'away_odds': away_odds,
            'handicap': handicap,
            'total_line': total_line,
            'match_time': datetime.now().isoformat()
        }


def main():
    """主函数"""
    collector = BasketballDataCollector()
    
    print("=== 篮球数据收集器 ===")
    
    teams = collector.fetch_teams_from_api()
    if teams:
        print(f"获取到 {len(teams)} 支球队数据")
        
        processed_teams = collector.process_all_teams()
        print("\n球队数据处理完成:")
        for team in processed_teams[:5]:
            print(f"  - {team['team_name']}: 胜率{team['winrate']*100:.1f}%, 场均{team['ppg']:.1f}分")
        
        league_avg = collector.calculate_league_averages()
        print(f"\n联盟平均数据:")
        print(f"  场均得分: {league_avg['avg_ppg']:.1f}")
        print(f"  场均失分: {league_avg['avg_oppg']:.1f}")
        print(f"  比赛节奏: {league_avg['avg_pace']:.1f}")
        
        match_data = collector.generate_match_data(
            "洛杉矶 湖人", "金州 勇士",
            home_odds=1.85, away_odds=2.10,
            handicap=-5.5, total_line=225.5
        )
        print(f"\n比赛数据生成:")
        print(json.dumps(match_data, ensure_ascii=False, indent=2))
    else:
        print("未能获取球队数据，请确保后端服务正在运行")


if __name__ == '__main__':
    main()
