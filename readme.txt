修改响应，使得响应中的响应头变为可以进行设置的
这样才能根据实际情况响应不同的内容

1：在HttpResponse中定义一个属性：Map headers
其中key保存响应头的名字，value保存对应的值

2：对外提供get,set方法。

3：修改sendHeaders方法，将原有的代码改变为根据headers中实际保存的响应头来进行发送。