#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
篮球比赛预测器
适配自足球预测系统，针对篮球特点进行重构
"""

import json
import numpy as np
from typing import Dict, List, Optional, Tuple
from dataclasses import dataclass
from feature_engineering import BasketballFeatureEngineering


@dataclass
class PredictionResult:
    """预测结果数据类"""
    home_team: str
    away_team: str
    
    home_win_prob: float
    away_win_prob: float
    prediction: str
    confidence: float
    
    handicap_line: Optional[float] = None
    home_cover_prob: Optional[float] = None
    away_cover_prob: Optional[float] = None
    
    total_line: Optional[float] = None
    expected_total: Optional[float] = None
    over_prob: Optional[float] = None
    under_prob: Optional[float] = None
    
    score_diff_prediction: Optional[str] = None
    overtime_prob: Optional[float] = None
    
    value_bet: Optional[str] = None
    expected_values: Optional[Dict] = None
    
    analysis: Optional[str] = None


class BasketballMatchPredictor:
    """篮球比赛预测器"""
    
    def __init__(self):
        self.feature_engineering = BasketballFeatureEngineering()
        self.home_court_advantage = 3.5
    
    def predict(self, home_team: Dict, away_team: Dict,
                home_odds: Optional[float] = None,
                away_odds: Optional[float] = None,
                handicap: Optional[float] = None,
                total_line: Optional[float] = None) -> PredictionResult:
        """执行完整预测"""
        
        win_result = self.feature_engineering.calculate_win_probability(
            home_team, away_team, home_odds, away_odds
        )
        
        handicap_result = self.feature_engineering.calculate_handicap_probability(
            home_team, away_team, handicap
        )
        
        total_result = self.feature_engineering.calculate_total_probability(
            home_team, away_team, total_line
        )
        
        diff_result = self.feature_engineering.calculate_score_diff_probability(
            home_team, away_team
        )
        
        overtime_prob = self.feature_engineering.calculate_overtime_probability(
            home_team, away_team
        )
        
        value_bet = None
        expected_values = None
        if home_odds and away_odds:
            home_ev = win_result['home_win_prob'] / 100 * home_odds - 1
            away_ev = win_result['away_win_prob'] / 100 * away_odds - 1
            
            expected_values = {
                'home_expected_value': round(home_ev * 100, 1),
                'away_expected_value': round(away_ev * 100, 1)
            }
            
            if home_ev > 0:
                value_bet = f"主胜(期望值+{round(home_ev * 100)}%)"
            elif away_ev > 0:
                value_bet = f"客胜(期望值+{round(away_ev * 100)}%)"
            else:
                value_bet = "无价值投注"
        
        analysis = self._generate_analysis(
            home_team, away_team, win_result, handicap_result, 
            total_result, diff_result, overtime_prob
        )
        
        return PredictionResult(
            home_team=home_team.get('teamname', '主队'),
            away_team=away_team.get('teamname', '客队'),
            
            home_win_prob=win_result['home_win_prob'],
            away_win_prob=win_result['away_win_prob'],
            prediction=win_result['prediction'],
            confidence=win_result['confidence'],
            
            handicap_line=handicap_result['handicap_line'],
            home_cover_prob=handicap_result['home_cover_prob'],
            away_cover_prob=handicap_result['away_cover_prob'],
            
            total_line=total_result['total_line'],
            expected_total=total_result['expected_total'],
            over_prob=total_result['over_prob'],
            under_prob=total_result['under_prob'],
            
            score_diff_prediction=diff_result['most_likely'],
            overtime_prob=overtime_prob,
            
            value_bet=value_bet,
            expected_values=expected_values,
            
            analysis=analysis
        )
    
    def predict_matches(self, matches: List[Dict]) -> List[PredictionResult]:
        """批量预测多场比赛"""
        results = []
        for match in matches:
            home_team = match.get('home_team', {})
            away_team = match.get('away_team', {})
            home_odds = match.get('home_odds')
            away_odds = match.get('away_odds')
            handicap = match.get('handicap')
            total_line = match.get('total_line')
            
            result = self.predict(
                home_team, away_team, 
                home_odds, away_odds,
                handicap, total_line
            )
            results.append(result)
        
        return results
    
    def find_value_bets(self, matches: List[Dict], 
                        min_ev: float = 0.05) -> List[Dict]:
        """寻找价值投注"""
        value_bets = []
        
        for match in matches:
            result = self.predict(
                match.get('home_team', {}),
                match.get('away_team', {}),
                match.get('home_odds'),
                match.get('away_odds')
            )
            
            if result.expected_values:
                home_ev = result.expected_values['home_expected_value'] / 100
                away_ev = result.expected_values['away_expected_value'] / 100
                
                if home_ev >= min_ev:
                    value_bets.append({
                        'match': f"{result.home_team} vs {result.away_team}",
                        'bet_type': '主胜',
                        'odds': match.get('home_odds'),
                        'probability': result.home_win_prob,
                        'expected_value': f"+{result.expected_values['home_expected_value']}%",
                        'confidence': result.confidence
                    })
                
                if away_ev >= min_ev:
                    value_bets.append({
                        'match': f"{result.home_team} vs {result.away_team}",
                        'bet_type': '客胜',
                        'odds': match.get('away_odds'),
                        'probability': result.away_win_prob,
                        'expected_value': f"+{result.expected_values['away_expected_value']}%",
                        'confidence': result.confidence
                    })
        
        return value_bets
    
    def _generate_analysis(self, home_team: Dict, away_team: Dict,
                          win_result: Dict, handicap_result: Dict,
                          total_result: Dict, diff_result: Dict,
                          overtime_prob: float) -> str:
        """生成分析报告"""
        lines = []
        
        lines.append(f"## 比赛分析: {home_team.get('teamname', '主队')} vs {away_team.get('teamname', '客队')}")
        lines.append("")
        
        lines.append("### 球队数据对比")
        lines.append("| 指标 | 主队 | 客队 |")
        lines.append("|------|------|------|")
        
        def safe_format(value, suffix=""):
            if value is None:
                return "-"
            try:
                return f"{float(value):.1f}{suffix}"
            except:
                return "-"
        
        lines.append(f"| 胜率 | {safe_format(home_team.get('winrate'), '%')} | {safe_format(away_team.get('winrate'), '%')} |")
        lines.append(f"| 场均得分 | {safe_format(home_team.get('ppg'))} | {safe_format(away_team.get('ppg'))} |")
        lines.append(f"| 场均失分 | {safe_format(home_team.get('oppg'))} | {safe_format(away_team.get('oppg'))} |")
        lines.append(f"| 进攻效率 | {safe_format(home_team.get('offRating'))} | {safe_format(away_team.get('offRating'))} |")
        lines.append(f"| 防守效率 | {safe_format(home_team.get('defRating'))} | {safe_format(away_team.get('defRating'))} |")
        lines.append(f"| 比赛节奏 | {safe_format(home_team.get('pace'))} | {safe_format(away_team.get('pace'))} |")
        lines.append("")
        
        lines.append("### 预测结果")
        lines.append(f"- **胜负预测**: {win_result['prediction']} (主胜{win_result['home_win_prob']}% / 客胜{win_result['away_win_prob']}%)")
        lines.append(f"- **信心指数**: {win_result['confidence']}%")
        lines.append(f"- **让分预测**: {handicap_result['prediction']} (让分线{handicap_result['handicap_line']})")
        lines.append(f"- **大小分预测**: {total_result['prediction']} (预期总分{total_result['expected_total']})")
        lines.append(f"- **胜分差预测**: {diff_result['most_likely']}")
        lines.append(f"- **加时概率**: {overtime_prob}%")
        lines.append("")
        
        lines.append("### 分析要点")
        home_off = home_team.get('offRating', 110)
        away_def = away_team.get('defRating', 110)
        if home_off and away_def:
            if home_off > away_def:
                lines.append(f"- 主队进攻效率({home_off})高于客队防守效率({away_def})，主队得分预期较高")
            else:
                lines.append(f"- 客队防守效率({away_def})能够限制主队进攻({home_off})")
        
        home_pace = home_team.get('pace', 100)
        away_pace = away_team.get('pace', 100)
        if home_pace and away_pace:
            avg_pace = (home_pace + away_pace) / 2
            if avg_pace > 102:
                lines.append(f"- 两队节奏较快(平均{avg_pace:.1f})，预期高分比赛")
            elif avg_pace < 98:
                lines.append(f"- 两队节奏较慢(平均{avg_pace:.1f})，预期低分比赛")
        
        return "\n".join(lines)
    
    def to_dict(self, result: PredictionResult) -> Dict:
        """将预测结果转换为字典"""
        return {
            'home_team': result.home_team,
            'away_team': result.away_team,
            'win_lose': {
                'home_win_prob': result.home_win_prob,
                'away_win_prob': result.away_win_prob,
                'prediction': result.prediction,
                'confidence': result.confidence
            },
            'handicap': {
                'handicap_line': result.handicap_line,
                'home_cover_prob': result.home_cover_prob,
                'away_cover_prob': result.away_cover_prob,
                'prediction': '让分主胜' if result.home_cover_prob and result.home_cover_prob > 50 else '让分客胜'
            },
            'total_score': {
                'total_line': result.total_line,
                'expected_total': result.expected_total,
                'over_prob': result.over_prob,
                'under_prob': result.under_prob,
                'prediction': '大分' if result.over_prob and result.over_prob > 50 else '小分'
            },
            'score_diff': {
                'prediction': result.score_diff_prediction
            },
            'overtime_probability': result.overtime_prob,
            'value_bet': result.value_bet,
            'expected_values': result.expected_values,
            'analysis': result.analysis
        }


def main():
    """主函数"""
    predictor = BasketballMatchPredictor()
    
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
    
    print("=" * 60)
    print("篮球比赛预测系统 - 经典模式")
    print("=" * 60)
    
    result = predictor.predict(
        home_team, away_team,
        home_odds=1.85,
        away_odds=2.10,
        handicap=-5.5,
        total_line=225.5
    )
    
    print(f"\n比赛: {result.home_team} vs {result.away_team}")
    print(f"\n【胜负预测】")
    print(f"  主胜概率: {result.home_win_prob}%")
    print(f"  客胜概率: {result.away_win_prob}%")
    print(f"  预测结果: {result.prediction}")
    print(f"  信心指数: {result.confidence}%")
    
    print(f"\n【让分预测】")
    print(f"  让分线: {result.handicap_line}")
    print(f"  让分主胜概率: {result.home_cover_prob}%")
    print(f"  让分客胜概率: {result.away_cover_prob}%")
    
    print(f"\n【大小分预测】")
    print(f"  总分线: {result.total_line}")
    print(f"  预期总分: {result.expected_total}")
    print(f"  大分概率: {result.over_prob}%")
    print(f"  小分概率: {result.under_prob}%")
    
    print(f"\n【胜分差预测】")
    print(f"  最可能分差: {result.score_diff_prediction}")
    
    print(f"\n【加时概率】")
    print(f"  加时概率: {result.overtime_prob}%")
    
    if result.value_bet:
        print(f"\n【价值投注】")
        print(f"  {result.value_bet}")
    
    print(f"\n【分析报告】")
    print(result.analysis)
    
    print("\n" + "=" * 60)
    print("JSON输出:")
    print(json.dumps(predictor.to_dict(result), ensure_ascii=False, indent=2))


if __name__ == '__main__':
    main()
