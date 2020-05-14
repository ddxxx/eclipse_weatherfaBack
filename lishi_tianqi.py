import time

from bs4 import BeautifulSoup
import requests
import pymysql
import warnings
from pypinyin import pinyin, lazy_pinyin

# 忽略警告信息
warnings.filterwarnings("ignore")
# 连接MySQL
conn = pymysql.connect(host='39.98.109.216', user='root', passwd='001997abc', db='weatherfa2', port=3306,
                       charset='utf8')
cursor = conn.cursor()


# 获取

def get_month_info(url, city):
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML,  like Gecko) '
                      'Chrome/63.0.3239.132 Safari/537.36'}  # 设置头文件信息，伪装浏览器
    try:
        response = requests.get(url, headers=headers).content  # 提交requests get 请求
        soup = BeautifulSoup(response, "html.parser")  # 用Beautifulsoup 进行解析
        resultset = soup.findAll('div', class_='lishitable')  # 表的上一级class是wdetail
        for info in resultset:
            li_list = info.find_all('li')[5:-1]  # 使用切片取到第2个tr标签（从第2个开始是天气数据）
            for li in enumerate(li_list):  # enumerate可以返回元素的位置及内容
                div_list = li[1].find_all('div')
                date = div_list[0].text.strip()
                d_temp = div_list[1].text.strip()
                n_temp = div_list[2].text.strip()
                weather = div_list[3].text.strip()
                wind = div_list[4].text.strip()
                print(city, date)
                cursor.execute('insert ignore into history_weather19(city, date, weather, '
                               'd_temp, n_temp, wind) values(%s, %s, %s, %s, %s, %s)'
                               , (city, date, weather, d_temp, n_temp, wind))
    except requests.exceptions.ConnectionError:
        print('ConnectionError -- please wait 3 seconds')
        time.sleep(3)
    except requests.exceptions.ChunkedEncodingError:
        print('ChunkedEncodingError -- please wait 3 seconds')
        time.sleep(3)


if __name__ == '__main__':
    # url = 'http://lishi.tianqi.com/' + 'dongping' + '/201801.html'
    # get_month_info(url, '东平')
    # conn.commit()
    cur = conn.cursor()
    cur.execute("select citypinyin, city from history_citylist_2")
    result = cur.fetchall()  # 获取表中所有数据
    for citypinyin, city in result:
        print(city, citypinyin)
        for year in range(2018, 2020):
            for month in range(1, 13):
                y_m = str(year) + str(month).zfill(2)
                url = 'http://lishi.tianqi.com/' + citypinyin + '/' + y_m + '.html'
                get_month_info(url, city)
            cur.execute("delete from history_citylist_2 where city = '%s'" % city)
            conn.commit()

        # for year in range(2019, 2020):
        #     for month in range(1, 12):
        #         y_m = str(year) + str(month).zfill(2)
        #         url = ['http://www.tianqihoubao.com/lishi/' + citypinyin + '/month/' +
        #                y_m + '.html']
        #         get_month_info(url, citypinyin)
        #         if year == 2020 & month == 4:
        #             break
        #     conn.commit()
