Subsampling Scale Image View魔改版
===========================
基于[SubsampleScaleImageView](https://github.com/davemorrissey/subsampling-scale-image-view/releases)的魔改版，目标是实现仿微信的大图浏览效果
## 情况分析：
1. 第一种情况，本地图片，即通过ImageSource.Resource()传进来的图片，这种图片开启了默认的Tile
* Tile是游戏开发里面经常运用的切片渲染技术
* 这种情况还没有想到良好的解决方案
2. 第二种情况，网络图片，例如 使用Glide先下载Bitmap，那么默认的Render会禁使用Tile
* 但是这种情况比较好解决，我们可以模仿ChangImageTransform自定义实现一个ScaleImageTransition
* 通过源码分析，ImageView是通过对BitmapDrawable的matrix进行concat而进行的，那么我们自己添加一个相应的矩阵
成员变量进行类似的操作
## 实践与问题
1. 无法获取到Bitmap的可见区域：
* 通过对矩阵区值与自己计算缩放比获取真实坐标
2. 在启动转换动画时获取到的开始值和结束值都是Null：
* 原因：capture**Value方法是在目标Activity中进行的，因此获取不到开始的值
* 解决:我们可以在启动时通过Intent传Bundle过去，在目标Activity的onSharedElemnetStart方法中获取这个值，通过
setTag的方法设置给View，同时很重要的一点是要在相应的结束方法里面设置view的tag为null，否则会捕获到两个相同的值
