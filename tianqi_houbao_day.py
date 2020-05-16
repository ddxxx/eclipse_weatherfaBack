# 获取天气后报昨天的历史天气数据。

import time

from bs4 import BeautifulSoup
import requests
import pymysql
from datetime import datetime, date, timedelta
import warnings
from pypinyin import pinyin, lazy_pinyin

# 忽略警告信息
warnings.filterwarnings("ignore")
# 连接MySQL
conn = pymysql.connect(host='localhost', user='root', passwd='001997', db='weatherfa', port=3306, charset='utf8')
cursor = conn.cursor()


# 获取

def get_month_info(url, city, date):
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML,  like Gecko) '
                      'Chrome/63.0.3239.132 Safari/537.36'}  # 设置头文件信息，伪装浏览器
    try:
        response = requests.get(url, headers=headers, timeout=10)  # .content  # 提交requests get 请求
        soup = BeautifulSoup(response.content, from_encoding="UTF-8")  # 用Beautifulsoup 进行解析
        resultset = soup.findAll('div', class_='wdetail')  # 表的上一级class是wdetail

        for info in resultset:
            tr_list = info.find_all('tr')[1:]  # 使用切片取到第2个tr标签（从第2个开始是天气数据）
            # 0:名称，1:白天, 2:夜间
            td_list_1 = tr_list[0].find_all('td')[1:]
            weather = td_list_1[0].text.strip().replace("\n", "")

            td_list_2 = tr_list[1].find_all('td')[1:]
            d_temp = td_list_2[0].text.strip().replace("\n", "").replace("℃", "")
            n_temp = td_list_2[1].text.strip().replace("\n", "").replace("℃", "")

            td_list_3 = tr_list[2].find_all('td')[1:]
            wind = td_list_3[0].text.strip().replace("\n", "")

            print(city, date, weather, d_temp, n_temp, wind)
            if weather != '--':
                cursor.execute('insert ignore into temp_history(city, date, weather, '
                               'd_temp, n_temp, wind) values(%s, %s, %s, %s, %s, %s) '
                               , (city, date, weather, d_temp, n_temp, wind))

    except requests.exceptions.ConnectTimeout:
        print('timeout')
        time.sleep(2)
        get_month_info(url, city, date)
    except requests.exceptions.ConnectionError:
        print('ConnectionError -- please wait 3 seconds')
        time.sleep(3)
        get_month_info(url, city, date)
    except requests.exceptions.ChunkedEncodingError:
        print('ChunkedEncodingError -- please wait 3 seconds')
        time.sleep(3)
        get_month_info(url, city, date)
    except requests.exceptions.HTTPError:
        print("Httperror")
        time.sleep(2)
        get_month_info(url, city, date)
    except requests.exceptions.RetryError:
        print("RetryError")
        time.sleep(2)
        get_month_info(url, city, date)
    except requests.exceptions.Timeout:
        print('Timeout')
        time.sleep(3)
        get_month_info(url, city, date)
    except requests.exceptions.RequestException:
        print('RequestException')
        time.sleep(2)
        get_month_info(url, city, date)



    # except:
    #     print('Unfortunitely -- An Unknow Error Happened, Please wait 3 seconds')
    #     time.sleep(3)


if __name__ == '__main__':
    cur = conn.cursor()
    cur.execute("select citypinyin, city from table_citylist_copy")
    result = cur.fetchall()  # 获取表中所有数据

    # 获取前一天的日期
    today = datetime.today()
    yesterday = today + timedelta(days=-1)
    date = str(yesterday).split(" ")[0]
    date_url = date.replace("-", "")

    for citypinyin, city in result:
        # print(citypinyin, city)
        url = 'http://www.tianqihoubao.com/lishi/' + citypinyin + "/" + date_url + '.html'
        # print(url)
        get_month_info(url, city, date)
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
