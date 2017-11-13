## Aequorea

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d18e09c5d4b44f749dce50f8db129c20)](https://www.codacy.com/app/nichbar/Aequorea?utm_source=github.com&utm_medium=referral&utm_content=nichbar/Aequorea&utm_campaign=badger)

> Aequorea  /ɪ`kɔ:riə/
>
> 一类多管水母。

自高中第一次翻阅一财开始就爱上了看一财的文章，觉得一财的 APP 不太好用，于是就写了一个自用。

如果你也与我一样喜欢看一财的文章又对官方客户端不满意，不妨到 [Release](https://github.com/nichbar/Aequorea/releases) 里下载新的安装包，体验更纯粹的阅读。

### Feature 

* 文章检索
* 文章转图片
* 离线缓存阅读
* 浅色与深色主题
* Material Design 风格

### Preview

![preview_1](preview/preview_1.png)

### FAQ	

1. Q：为什么文章搜索`htc`没有结果？

   A：因为一财的 API 搜索对大小写敏感，错一个字符都搜不到。（笑

2. Q：为什么图片很小加载却有点慢？

   A：官方没对图片做处理，给的都是大图。（虽然大但不保证清晰度）

3. Q：为什么这么耗流量？

   A：因为接口返回的都是大图，推荐在流量充足的时候使用。（1.3.3 开始支持离线缓存了）

4. Q：为什么作者页里明明显示有5篇文章，但是实际列表里只有4篇？

   A：因为该作者的部分文章要求订阅用户才能阅读，Aequorea 对这类文章进行了过滤处理，仅展示可读文章。


### Thanks

- [RxJava](https://github.com/ReactiveX/RxJava)
- [RxAndroid](https://github.com/ReactiveX/RxAndroid)
- [OkHttp](https://github.com/square/okhttp)
- [Retrofit](https://github.com/square/retrofit)
- [Gson](https://github.com/google/gson)
- [ButterKnife](https://github.com/JakeWharton/butterknife)
- [RichText](https://github.com/zzhoujay/RichText)
- [MySplash](https://github.com/WangDaYeeeeee/Mysplash)
- [Newsfold](https://play.google.com/store/apps/details?id=it.mvilla.android.quote&hl=en)
