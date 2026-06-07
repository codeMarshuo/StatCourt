#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
篮球比赛预测系统 - 特征工程模块
适配自足球预测系统，针对篮球特点进行重构
"""

import pandas as pd
import numpy as np
from typing import Dict, List, Tuple, Optional

class BasketballFeatureEngineering:
    """篮球比赛特征工程"""
    
    def __init__(self):
        self.feature_columns = [
            'home_win_rate', 'away_win_rate',
            'home_ppg', 'away_ppg',
            'home_oppg', 'away_oppg',
            'home_off_rating', 'away_off_rating',
            'home_def_rating', 'away_def_rating',
            'home_pace', 'away_pace',
            'home_point_diff', 'away_point_diff',
            'home_recent_form', 'away_recent_form',
            'h2h_home_wins', 'h2h_away_wins',
            'home_back_to_back', 'away_back_to_back',
            'home_rest_days', 'away_rest_days'
        ]
    
    def extract_features(self, home_team: Dict, away_team: Dict, 
                        h2h_data: Optional[List] = None) -> np.ndarray:
        """提取比赛特征"""
        features = []
        
        features.append(self._safe_get(home_team, 'winrate', 0.5))
        features.append(self._safe_get(away_team, 'winrate', 0.5))
        
        features.append(self._safe_get(home_team, 'ppg', 110.0))
        features.append(self._safe_get(away_team, 'ppg', 110.0))
        
        features.append(self._safe_get(home_team, 'oppg', 110.0))
        features.append(self._safe_get(away_team, 'oppg', 110.0))
        
        features.append(self._safe_get(home_team, 'offRating', 110.0))
        features.append(self._safe_get(away_team, 'offRating', 110.0))
        
        features.append(self._safe_get(home_team, 'defRating', 110.0))
        features.append(self._safe_get(away_team, 'defRating', 110.0))
        
        features.append(self._safe_get(home_team, 'pace', 100.0))
        features.append(self._safe_get(away_team, 'pace', 100.0))
        
        features.append(self._safe_get(home_team, 'pointdiff', 0.0))
        features.append(self._safe_get(away_team, 'pointdiff', 0.0))
        
        features.append(self._calculate_recent_form(home_team))
        features.append(self._calculate_recent_form(away_team))
        
        h2h_features = self._calculate_h2h(h2h_data) if h2h_data else (0.5, 0.5)
        features.extend(h2h_features)
        
        features.extend([0, 0])
        features.extend([2, 2])
        
        return np.array(features)
    
    def _safe_get(self, data: Dict, key: str, default: float) -> float:
        """安全获取字典值"""
        value = data.get(key)
        if value is None:
            return default
        try:
            return float(value)
        except (ValueError, TypeError):
            return default
    
    def _calculate_recent_form(self, team: Dict) -> float:
        """计算球队近期状态"""
        wins = team.get('wins', 0)
        games = team.get('games', 1)
        if games == 0:
            return 0.5
        return wins / games
    
    def _calculate_h2h(self, h2h_data: List) -> Tuple[float, float]:
        """计算历史交锋数据"""
        if not h2h_data:
            return (0.5, 0.5)
        
        home_wins = sum(1 for match in h2h_data if match.get('home_win', False))
        away_wins = len(h2h_data) - home_wins
        total = len(h2h_data)
        
        return (home_wins / total, away_wins / total)
    
    def calculate_expected_score(self, home_team: Dict, away_team: Dict) -> Dict:
        """计算预期比分"""
        home_pace = self._safe_get(home_team, 'pace', 100.0)
        away_pace = self._safe_get(away_team, 'pace', 100.0)
        avg_pace = (home_pace + away_pace) / 2
        
        home_off = self._safe_get(home_team, 'offRating', 110.0)
        away_def = self._safe_get(away_team, 'defRating', 110.0)
        away_off = self._safe_get(away_team, 'offRating', 110.0)
        home_def = self._safe_get(home_team, 'defRating', 110.0)
        
        home_expected = avg_pace * (home_off + away_def) / 200
        away_expected = avg_pace * (away_off + home_def) / 200
        
        return {
            'home_expected_score': round(home_expected, 1),
            'away_expected_score': round(away_expected, 1),
            'total_expected': round(home_expected + away_expected, 1),
            'expected_diff': round(home_expected - away_expected, 1)
        }
    
    def calculate_win_probability(self, home_team: Dict, away_team: Dict,
                                  home_odds: Optional[float] = None,
                                  away_odds: Optional[float] = None) -> Dict:
        """计算胜负概率"""
        home_win_rate = self._safe_get(home_team, 'winrate', 0.5)
        away_win_rate = self._safe_get(away_team, 'winrate', 0.5)
        
        home_off = self._safe_get(home_team, 'offRating', 110.0)
        away_def = self._safe_get(away_team, 'defRating', 110.0)
        away_off = self._safe_get(away_team, 'offRating', 110.0)
        home_def = self._safe_get(home_team, 'defRating', 110.0)
        
        home_court_advantage = 3.5
        
        home_strength = home_off - away_def + home_court_advantage
        away_strength = away_off - home_def
        
        base_home_prob = 0.5 + (home_strength - away_strength) / 20
        base_home_prob = max(0.1, min(0.9, base_home_prob))
        
        if home_odds and away_odds and home_odds > 0 and away_odds > 0:
            implied_home = 1 / home_odds
            implied_away = 1 / away_odds
            total_implied = implied_home + implied_away
            
            if total_implied > 0:
                implied_home = implied_home / total_implied
                adjusted_home = (base_home_prob + implied_home) / 2
            else:
                adjusted_home = base_home_prob
        else:
            adjusted_home = base_home_prob
        
        adjusted_away = 1 - adjusted_home
        
        return {
            'home_win_prob': round(adjusted_home * 100, 1),
            'away_win_prob': round(adjusted_away * 100, 1),
            'prediction': '主胜' if adjusted_home > 0.5 else '客胜',
            'confidence': round(abs(adjusted_home - 0.5) * 200, 1)
        }
    
    def calculate_handicap_probability(self, home_team: Dict, away_team: Dict,
                                       handicap: Optional[float] = None) -> Dict:
        """计算让分概率"""
        home_diff = self._safe_get(home_team, 'pointdiff', 0)
        away_diff = self._safe_get(away_team, 'pointdiff', 0)
        
        expected_diff = (home_diff - away_diff) + 3.5
        
        if handicap is None:
            handicap = -expected_diff
        
        adjusted_diff = expected_diff + handicap
        
        home_cover_prob = self._normal_cdf(adjusted_diff / 8)
        away_cover_prob = 1 - home_cover_prob
        
        return {
            'handicap_line': handicap,
            'home_cover_prob': round(home_cover_prob * 100, 1),
            'away_cover_prob': round(away_cover_prob * 100, 1),
            'prediction': '让分主胜' if home_cover_prob > 0.5 else '让分客胜',
            'expected_diff': round(expected_diff, 1)
        }
    
    def calculate_total_probability(self, home_team: Dict, away_team: Dict,
                                    total_line: Optional[float] = None) -> Dict:
        """计算大小分概率"""
        expected = self.calculate_expected_score(home_team, away_team)
        expected_total = expected['total_expected']
        
        if total_line is None:
            total_line = expected_total
        
        std_dev = 10.0
        over_prob = 1 - self._normal_cdf((total_line - expected_total) / std_dev)
        under_prob = 1 - over_prob
        
        return {
            'total_line': total_line,
            'expected_total': expected_total,
            'over_prob': round(over_prob * 100, 1),
            'under_prob': round(under_prob * 100, 1),
            'prediction': '大分' if over_prob > 0.5 else '小分'
        }
    
    def _normal_cdf(self, x: float) -> float:
        """标准正态分布累积函数"""
        from math import erf, sqrt
        return 0.5 * (1 + erf(x / sqrt(2)))
    
    def calculate_score_diff_probability(self, home_team: Dict, away_team: Dict) -> Dict:
        """计算胜分差概率"""
        home_diff = self._safe_get(home_team, 'pointdiff', 0)
        away_diff = self._safe_get(away_team, 'pointdiff', 0)
        
        expected_diff = (home_diff - away_diff) + 3.5
        std_dev = 8.0
        
        intervals = [
            {'range': '1-5分', 'min': 0, 'max': 5},
            {'range': '6-10分', 'min': 5, 'max': 10},
            {'range': '11-15分', 'min': 10, 'max': 15},
            {'range': '16-20分', 'min': 15, 'max': 20},
            {'range': '21分以上', 'min': 20, 'max': float('inf')}
        ]
        
        predictions = []
        for interval in intervals:
            if interval['max'] == float('inf'):
                prob = 1 - self._normal_cdf((interval['min'] - abs(expected_diff)) / std_dev)
            else:
                prob = (self._normal_cdf((interval['max'] - abs(expected_diff)) / std_dev) -
                       self._normal_cdf((interval['min'] - abs(expected_diff)) / std_dev))
            
            predictions.append({
                'range': interval['range'],
                'probability': round(max(0, prob) * 100, 1)
            })
        
        most_likely = max(predictions, key=lambda x: x['probability'])
        
        return {
            'predictions': predictions,
            'expected_diff': round(expected_diff, 1),
            'most_likely': most_likely['range']
        }
    
    def calculate_overtime_probability(self, home_team: Dict, away_team: Dict) -> float:
        """计算加时概率"""
        home_win_rate = self._safe_get(home_team, 'winrate', 0.5)
        away_win_rate = self._safe_get(away_team, 'winrate', 0.5)
        
        strength_diff = abs(home_win_rate - away_win_rate)
        
        base_overtime = 0.06
        adjusted = base_overtime * (1 - strength_diff)
        
        return round(max(0.02, min(0.15, adjusted)) * 100, 1)


if __name__ == '__main__':
    fe = BasketballFeatureEngineering()
    
    home_team = {
        'teamname': '洛杉矶 湖人',
        'winrate': 0.65,
        'ppg': 115.5,
        'oppg': 110.2,
        'offRating': 115.0,
        'defRating': 108.5,
        'pace': 100.5,
        'pointdiff': 5.3
    }
    
    away_team = {
        'teamname': '金州 勇士',
        'winrate': 0.58,
        'ppg': 118.2,
        'oppg': 112.5,
        'offRating': 118.0,
        'defRating': 111.0,
        'pace': 102.3,
        'pointdiff': 5.7
    }
    
    print("=== 篮球比赛预测 ===")
    print(f"主队: {home_team['teamname']}")
    print(f"客队: {away_team['teamname']}")
    
    win_prob = fe.calculate_win_probability(home_team, away_team, 1.85, 2.10)
    print(f"\n胜负预测: {win_prob}")
    
    handicap = fe.calculate_handicap_probability(home_team, away_team, -5.5)
    print(f"让分预测: {handicap}")
    
    total = fe.calculate_total_probability(home_team, away_team, 225.5)
    print(f"大小分预测: {total}")
    
    diff = fe.calculate_score_diff_probability(home_team, away_team)
    print(f"胜分差预测: {diff}")
    
    overtime = fe.calculate_overtime_probability(home_team, away_team)
    print(f"加时概率: {overtime}%")
