## SlidingMenu
 一个仿手机qq的侧滑菜单的实现

![](https://github.com/a750183047/SlidingMenu/blob/master/image/image1.png?raw=true)


![](https://github.com/a750183047/SlidingMenu/blob/master/image/image2.png?raw=true)

![](https://github.com/a750183047/SlidingMenu/blob/master/image/image3.png?raw=true)



* 侧滑效果如图，在侧滑过程中，主View会有缩放效果。
* 在侧滑过程中有侧滑状态监听接口，可以在侧滑的过程中进行动作控制，如根据侧滑的程度改变头像的透明度等操作。
* 使用 ViewDragHelper 类进行侧滑的控制，通过重写 callbacdk 类中的各个方法控制侧滑的细节。
* 设置状态改变监听接口，能够在侧滑过程中对各个状态进行控制。
