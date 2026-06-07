#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
篮球预测系统配置文件
"""

import os

class Config:
    """配置类"""
    
    API_BASE_URL = os.environ.get('NBA_API_URL', 'http://localhost:8081')
    
    AI_API_KEY = os.environ.get('AI_API_KEY', '')
    AI_API_URL = os.environ.get('AI_API_URL', 'https://open.bigmodel.cn/api/paas/v4/chat/completions')
    AI_MODEL = os.environ.get('AI_MODEL', 'glm-4.7-flash')
    
    HOME_COURT_ADVANTAGE = 3.5
    SCORE_STD_DEV = 8.0
    TOTAL_STD_DEV = 10.0
    
    SCORE_DIFF_INTERVALS = [5, 10, 15, 20]
    SCORE_DIFF_LABELS = ["1-5分", "6-10分", "11-15分", "16-20分", "21分以上"]
    
    TOTAL_INTERVALS = [200, 220, 240, 260]
    TOTAL_LABELS = ["200分以下", "200-220分", "220-240分", "240-260分", "260分以上"]
    
    BASE_OVERTIME_PROB = 0.06
    MIN_OVERTIME_PROB = 0.02
    MAX_OVERTIME_PROB = 0.15
    
    MIN_VALUE_EV = 0.05
    
    DATA_CACHE_SECONDS = 300
    
    LOG_LEVEL = os.environ.get('LOG_LEVEL', 'INFO')
    LOG_FILE = os.environ.get('LOG_FILE', 'basketball_predictor.log')


class TeamConfig:
    """球队配置"""
    
    NBA_TEAMS = {
        'ATL': '亚特兰大 老鹰',
        'BOS': '波士顿 凯尔特人',
        'BKN': '布鲁克林 篮网',
        'CHA': '夏洛特 黄蜂',
        'CHI': '芝加哥 公牛',
        'CLE': '克利夫兰 骑士',
        'DAL': '达拉斯 独行侠',
        'DEN': '丹佛 掘金',
        'DET': '底特律 活塞',
        'GSW': '金州 勇士',
        'HOU': '休斯顿 火箭',
        'IND': '印第安纳 步行者',
        'LAC': '洛杉矶 快船',
        'LAL': '洛杉矶 湖人',
        'MEM': '孟菲斯 灰熊',
        'MIA': '迈阿密 热火',
        'MIL': '密尔沃基 雄鹿',
        'MIN': '明尼苏达 森林狼',
        'NOP': '新奥尔良 鹈鹕',
        'NYK': '纽约 尼克斯',
        'OKC': '俄克拉荷马城 雷霆',
        'ORL': '奥兰多 魔术',
        'PHI': '费城 76人',
        'PHX': '菲尼克斯 太阳',
        'POR': '波特兰 开拓者',
        'SAC': '萨克拉门托 国王',
        'SAS': '圣安东尼奥 马刺',
        'TOR': '多伦多 猛龙',
        'UTA': '犹他 爵士',
        'WAS': '华盛顿 奇才'
    }
    
    CONFERENCE = {
        'EAST': ['ATL', 'BOS', 'BKN', 'CHA', 'CHI', 'CLE', 'DET', 'IND', 'MIA', 'MIL', 'NYK', 'ORL', 'PHI', 'TOR', 'WAS'],
        'WEST': ['DAL', 'DEN', 'GSW', 'HOU', 'LAC', 'LAL', 'MEM', 'MIN', 'NOP', 'OKC', 'PHX', 'POR', 'SAC', 'SAS', 'UTA']
    }
    
    DIVISIONS = {
        'Atlantic': ['BOS', 'BKN', 'NYK', 'PHI', 'TOR'],
        'Central': ['CHI', 'CLE', 'DET', 'IND', 'MIL'],
        'Southeast': ['ATL', 'CHA', 'MIA', 'ORL', 'WAS'],
        'Northwest': ['DEN', 'MIN', 'OKC', 'POR', 'UTA'],
        'Pacific': ['GSW', 'LAC', 'LAL', 'PHX', 'SAC'],
        'Southwest': ['DAL', 'HOU', 'MEM', 'NOP', 'SAS']
    }


class BettingConfig:
    """投注配置"""
    
    BET_TYPES = {
        'WIN_LOSE': '胜负',
        'HANDICAP': '让分胜负',
        'TOTAL': '大小分',
        'SCORE_DIFF': '胜分差',
        'HALF_FULL': '半全场'
    }
    
    DEFAULT_HANDICAP = -5.5
    DEFAULT_TOTAL_LINE = 220.5
    
    HANDICAP_RANGE = [-15.5, 15.5]
    TOTAL_RANGE = [190.5, 250.5]


if __name__ == '__main__':
    print("=== 篮球预测系统配置 ===")
    print(f"API地址: {Config.API_BASE_URL}")
    print(f"AI模型: {Config.AI_MODEL}")
    print(f"主场优势: {Config.HOME_COURT_ADVANTAGE}")
    print(f"\nNBA球队数量: {len(TeamConfig.NBA_TEAMS)}")
    print(f"东部球队: {len(TeamConfig.CONFERENCE['EAST'])}")
    print(f"西部球队: {len(TeamConfig.CONFERENCE['WEST'])}")
