import time

from bs4 import BeautifulSoup
import requests
import pymysql
import datetime
import warnings
from pypinyin import pinyin, lazy_pinyin

# 忽略警告信息
warnings.filterwarnings("ignore")
# 连接MySQL
conn = pymysql.connect(host='localhost', user='root', passwd='001997', db='weatherfa', port=3306, charset='utf8')
cursor = conn.cursor()


# 获取

def get_month_info(url, city):
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML,  like Gecko) '
                      'Chrome/63.0.3239.132 Safari/537.36'}  # 设置头文件信息，伪装浏览器
    try:
        response = requests.get(url, headers=headers).content  # 提交requests get 请求
        soup = BeautifulSoup(response, "html.parser")  # 用Beautifulsoup 进行解析
        resultset = soup.findAll('div', class_='wdetail')  # 表的上一级class是wdetail

        for info in resultset:
            tr_list = info.find_all('tr')[1:]  # 使用切片取到第2个tr标签（从第2个开始是天气数据）
            for index, tr in enumerate(tr_list):  # enumerate可以返回元素的位置及内容
                td_list = tr.find_all('td')
                # if index == 0:

                date = td_list[0].text.strip().replace("\n", "")  # 取每个标签的text信息，并使用replace()函数将换行符删除
                date = date.replace("年", "-").replace("月", "-").replace("日", "")
                weather = td_list[1].text.strip().replace("\n", "").split("/")
                weather = weather[0].strip()
                temp = td_list[2].text.strip().replace("\n", "").split("/")
                d_temp = int(temp[0].replace("℃", "").strip())
                n_temp = int(temp[1].replace("℃", "").strip())
                owind = td_list[3].text.strip().replace("\n", "").split("/")
                wind = owind[0].strip()

                print(city, date, weather, d_temp, n_temp, wind)
                cursor.execute('insert ignore into temp_history(city, date, weather, '
                               'd_temp, n_temp, wind) values(%s, %s, %s, %s, %s, %s) '
                               , (city, date, weather, d_temp, n_temp, wind))
    except requests.exceptions.ConnectionError:
        print('ConnectionError -- please wait 3 seconds')
        time.sleep(3)
    except requests.exceptions.ChunkedEncodingError:
        print('ChunkedEncodingError -- please wait 3 seconds')
        time.sleep(3)
    except:
        print('Unfortunitely -- An Unknow Error Happened, Please wait 3 seconds')
        time.sleep(3)


if __name__ == '__main__':
    cur = conn.cursor()
    cur.execute("select citypinyin, city from table_citylist_1")
    result = cur.fetchall()  # 获取表中所有数据
    for citypinyin, city in result:
        print(city, citypinyin)
        y_m = str(datetime.datetime.now().year) + str(datetime.datetime.now().month).zfill(2)
        url = 'http://www.tianqihoubao.com/lishi/' + citypinyin + '/month/' + y_m + '.html'
        get_month_info(url, city)
        conn.commit()

        # for month in range(1, 5):
        #
        #     y_m = '2020' + str(month).zfill(2)
        #     url = 'http://www.tianqihoubao.com/lishi/' + citypinyin + '/month/' + y_m + '.html'
        #     get_month_info(url, city)
        #
        #
        # cur.execute("delete from history_citylist where city = '%s'" % city)
        # cur.execute("delete from history_citylist_2 where city = '%s'" % city)
        # conn.commit()
