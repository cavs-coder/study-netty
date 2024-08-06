

### NettyServer流程

### 问：ServerBootstrap是干什么的，为什么要单独给它进行配置？

`ServerBootstrap` 是 Netty 提供的一个便捷类，用于简化服务器端的配置和启动。它封装了许多底层细节，使开发者能够更容易地设置和启动一个 Netty 服务器。下面详细解释 `ServerBootstrap` 的作用以及为什么要单独给它进行配置：

### 作用

`ServerBootstrap` 的主要作用是：

1. **配置服务器**：它提供了一系列的方法，用于配置服务器的各种参数，如线程组、通道类型、选项等。
2. **绑定端口**：它可以绑定一个或多个端口，使服务器能够监听客户端的连接请求。
3. **设置通道初始化器**：它允许你为每个新连接设置一个通道初始化器，用于配置每个新创建的 `Channel`。



**使用 `ServerBootstrap` 配置服务器有以下几个好处：**

1. **简化代码**：`ServerBootstrap` 封装了底层的复杂性，使得代码更简洁、更易读。
2. **可扩展性**：你可以通过 `ServerBootstrap` 的各种配置方法，灵活地调整服务器的参数和行为。例如，你可以根据需要设置不同的线程模型、通道选项等。
3. **分离职责**：通过单独配置 `ServerBootstrap`，你可以将服务器的初始化和启动逻辑与其他业务逻辑分开，使得代码结构更清晰。

### 配置示例

下面是一个简单的示例，展示了如何使用 `ServerBootstrap` 配置和启动一个 Netty 服务器：

```java
@Slf4j
public class NettyServer {

    public static void main(String[] args) {
        new NettyServer().bind(8080);
    }

    private void bind(int port) {
        //配置服务端NIO线程组
        //1、处理客户端连接请求的线程组，也叫 boss group
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        //2、处理已接受连接的I/O操作（读写操作）的线程组，也叫 worker group
        EventLoopGroup childGroup = new NioEventLoopGroup();

        //创建服务端启动辅助类，用于简化服务端的通道配置
        ServerBootstrap b = new ServerBootstrap();
        //配置两个线程组
        b.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)//使用NioServerSocketChannel实例化一个新的通道对象，以接收传入的连接
                .option(ChannelOption.SO_BACKLOG, 128)//配置通道选项，这里设置的时服务端接收连接的队列长度
                .childHandler(new MyChannelInitializer());//配置子通道的处理器
        try {
            //绑定端口，并启动服务器，开始接收连接
            ChannelFuture f = b.bind(port).sync();//同步等待绑定完成，确保服务器正确启动
            log.info("server start done");
            //监听通道的关闭事件
            f.channel().closeFuture().sync();//同步等待通道关闭，确保服务器运行直到被关闭。
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //优雅关闭两个线程组
            childGroup.shutdownGracefully();
            parentGroup.shutdownGracefully();
        }
    }
}

@Slf4j
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 有新的客户端连接时，netty会调用这个方法，可以在这时候对
     */
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        log.info("连接报告开始");
        log.info("连接报告信息：有一客户端连接到本服务端");
        log.info("连接报告IP: {}" , channel.remoteAddress().getHostString());
        log.info("连接报告Port: {}" , channel.remoteAddress().getPort());
        log.info("连接报告完毕");
    }
}
```

在这个示例中，`ServerBootstrap` 被用来配置线程组、通道类型、通道选项和通道初始化器，然后通过 `bind` 方法绑定端口并启动服务器。这样的配置方式使得代码结构清晰，易于维护和扩展。

以下是每一步的详细解释：

1. **配置NIO线程组**：

   ```java
   EventLoopGroup parentGroup = new NioEventLoopGroup();
   EventLoopGroup childGroup = new NioEventLoopGroup();
   ```

   - `parentGroup` 处理客户端连接请求的线程组，也叫 boss group。接受连接后，将连接分配给 `childGroup` 来处理。

   - `childGroup` 处理已接受连接的I/O操作（读写操作）的线程组，也叫 worker group。每个连接都有自己的 `SocketChannel`，这些 `SocketChannel` 由 `childGroup` 中的线程处理。

     

     流程描述：

     - 当一个新的客户端连接到服务器时，`parentGroup` 中的一个线程会处理这个连接请求。
     - 连接请求被接受后，`parentGroup` 会将这个新的连接交给 `childGroup` 中的一个线程。
     - `childGroup` 中的线程负责处理这个连接的所有 I/O 操作（如读取客户端的数据，发送数据到客户端）。

     这样设计的好处包括：

     - **职责分离**：`parentGroup` 专注于处理连接请求，而 `childGroup` 专注于处理数据的读写操作。职责分离使得代码更简洁，逻辑更清晰。

     - **提高性能**：这种分工可以利用多核 CPU 的优势。`parentGroup` 可以迅速接受新的连接，而 `childGroup` 可以并行处理多个连接的数据操作。

     - **可扩展性**：可以根据需要独立调整 `parentGroup` 和 `childGroup` 的线程数，以优化性能。例如，可以根据连接数和数据流量调整线程数。

       

2. **创建ServerBootstrap**：

   ```java
   ServerBootstrap b = new ServerBootstrap();
   ```

   - `ServerBootstrap` 是一个简化服务器配置的类，提供了一系列的方法用于设置服务器。

3. **配置ServerBootstrap**：

   ```java
   b.group(parentGroup, childGroup)
    .channel(NioServerSocketChannel.class)
    .option(ChannelOption.SO_BACKLOG, 128)
    .childHandler(new MyChannelInitializer());
   ```

   - `group(parentGroup, childGroup)`：设置NIO线程组。
   - `channel(NioServerSocketChannel.class)`：指定通道类型为NIO。
   - `option(ChannelOption.SO_BACKLOG, 128)`：设置通道选项，例如连接队列的大小。
   - `childHandler(new MyChannelInitializer())`：设置子通道的处理器，负责初始化每一个新的连接。

4. **绑定端口并启动服务器**：

   ```java
   ChannelFuture f = b.bind(port).sync();
   ```

   - `bind(port)`：绑定服务器端口。
   - `sync()`：同步等待绑定完成，确保服务器正确启动。

5. **打印服务器启动信息**：

   ```java
   System.out.println("itstack-demo-netty server start done. {关注公众号：bugstack虫洞栈，获取源码}");
   ```

6. **等待服务器通道关闭**：

   ```java
   f.channel().closeFuture().sync();
   ```

   - `closeFuture().sync()`：同步等待通道关闭，确保服务器运行直到被关闭。

7. **捕获异常并优雅关闭线程组**：

   ```java
   } catch (InterruptedException e) {
       e.printStackTrace();
   } finally {
       childGroup.shutdownGracefully();
       parentGroup.shutdownGracefully();
   }
   ```

   - 捕获 `InterruptedException` 并打印堆栈跟踪。

   - 在 `finally` 块中优雅关闭线程组，释放资源。

     

这个流程是启动Netty服务器的标准流程，确保服务器能够正确处理客户端连接和I/O操作，同时也提供了异常处理和资源释放的机制。