 - ip:39.108.222.183(www.irran.top)
 - port:8080
 - source code:https://github.com/Irran/speedread

---

###查询test_books的列表
request

~~~json
{
    "cmd":"check",
    "type":"test_books"
}
~~~

response

~~~json
{
    "resource":"list",
    "type":"test_books",
    "content":[["review filename",
    				"base64 coded md5 review",
    				"image filename",
    				"base64 coded md5 image"]]
}
~~~
content里面放的是具体列表的信息，其中每一项是一个数组，这个数组中包含4个分量

第一个和第三个分量是文件名，也就是test_book/resources.txt中每一行的那两个文件名
第二和第四个分量代表文件具体内容，我把具体内容通过md5散列了一下，然后转成base64格式返回。客户端把本地文件进行相同处理后进行比较，就能得知是否这段文字/图片的具体内容有变化

###查询class_training的列表

request

~~~json
{
    "cmd":"check",
    "type":"class_training",
    "level":1
}
~~~

response

~~~json
{
    "resource":"list",
    "type":"class_training",
    "level":1,
    "content":[["name1","base64 coded md5 content1"],
                ["name2","base64 coded md5 content2"],
                ["name3","base64 coded md5 content3"]]
}
~~~

level字段代表class_training的某个子文件夹

比如说level为1，就会返回class_training/level_1/resources.txt中的内容

content还是代表具体内容，其中每一项是一个数组，这个数组中包含2个分量，第一个是文件名，第二个是文件具体内容，编码方式和上面说的一样

###查询资源

request

~~~json
{
    "cmd":"get_resource",
    "type":"test_books",
    "tag":"image/review",
    "name":"xxxxx"
}

{
    "cmd":"get_resource",
    "type":"class_training",
    "level":1,
    "name":"xxxxx"
}
~~~

response

~~~json
{
    "resource":"book/review/image",
    "name":"name",
    "content":"xxxxx"
}
~~~

我看了一下一共有三种资源（test_books中的review、image、class_training中的书籍内容），所以就设立了上述两种查询的语法，tag用来标识是想要image还是review，永远只能取二者之一。name代表想要获取的资源的文件名

他们的返回格式是一样的，content中是具体内容，这里就是直接返回了原本文件中存的内容，没有做任何编码。如果想要图片的话，应该发送`"name":"xxx.base64"`这样的指令

---
example

查询test_books的列表

[http://www.irran.top:8080/speedread/?jsonstr=%7B%22cmd%22%3A%22check%22%2C%22type%22%3A%22test\_books%22%7D](http://www.irran.top:8080/speedread/?jsonstr=%7B%22cmd%22%3A%22check%22%2C%22type%22%3A%22test_books%22%7D)

查询class\_training level=1的列表

[http://www.irran.top:8080/speedread/?jsonstr=%7B%22cmd%22%3A%22check%22%2C%22type%22%3A%22class\_training%22%2C%22level%22%3A2%7D](http://www.irran.top:8080/speedread/?jsonstr=%7B%22cmd%22%3A%22check%22%2C%22type%22%3A%22class_training%22%2C%22level%22%3A2%7D)

查询class\_training level=1 name=11_18颗樱桃.txt的具体内容

[http://www.irran.top:8080/speedread/?jsonstr=%7B%22cmd%22%3A%22get\_resource%22%2C%22type%22%3A%22class\_training%22%2C%22level%22%3A1%2C%22name%22%3A%2211_18%E9%A2%97%E6%A8%B1%E6%A1%83.txt%22%7D](http://www.irran.top:8080/speedread/?jsonstr=%7B%22cmd%22%3A%22get_resource%22%2C%22type%22%3A%22class_training%22%2C%22level%22%3A1%2C%22name%22%3A%2211_18%E9%A2%97%E6%A8%B1%E6%A1%83.txt%22%7D)

查询图片icon_0.base64具体信息

[http://www.irran.top:8080/speedread/?jsonstr=%7B%22cmd%22%3A%22get\_resource%22%2C%22type%22%3A%22test\_books%22%2C%22tag%22%3A%22image%22%2C%22name%22%3A%22icon_0.base64%22%7D](http://www.irran.top:8080/speedread/?jsonstr=%7B%22cmd%22%3A%22get_resource%22%2C%22type%22%3A%22test_books%22%2C%22tag%22%3A%22image%22%2C%22name%22%3A%22icon_0.base64%22%7D)

---
因为貌似原文件是用gbk编码的，所以从网页返回的数据也用了gbk
