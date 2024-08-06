在 Netty 中，客户端和服务端的配置有一些不同，这是因为它们的职责和工作模式不同。具体来说：

1. **服务端的配置**：
   - 服务端需要处理多个客户端连接。因此，它使用两个事件循环组（`EventLoopGroup`）：
     - `bossGroup`：接受传入的连接请求。
     - `workerGroup`：处理已接受连接的 I/O 操作（读写操作）。
   - 服务端使用 `ServerBootstrap` 来配置和启动服务器。
   - 服务端需要配置两个处理器：
     - `childHandler`：处理新连接的 `Channel`，也就是子通道（子通道是指每一个客户端连接到服务端后服务端创建的与客户端通信的通道）。

   示例：
   ```java
   ServerBootstrap b = new ServerBootstrap();
   b.group(bossGroup, workerGroup)
    .channel(NioServerSocketChannel.class)
    .option(ChannelOption.SO_BACKLOG, 128)
    .childHandler(new MyChannelInitializer());
   ```

2. **客户端的配置**：
   - 客户端只需要一个事件循环组（`EventLoopGroup`），因为它只需要处理一个连接的 I/O 操作。
   - 客户端使用 `Bootstrap` 来配置和启动客户端。
   - 客户端只需要一个处理器：`handler`，用于处理通道的 I/O 操作。

   示例：
   ```java
   Bootstrap b = new Bootstrap();
   b.group(workerGroup)
    .channel(NioSocketChannel.class)
    .option(ChannelOption.AUTO_READ, true)
    .handler(new MyChannelInitializer());
   ```

### 为什么客户端没有 `childHandler`

这是因为客户端和服务端的职责不同：

- **服务端**：需要接受多个客户端的连接，每个连接会创建一个新的子通道。`childHandler` 用于初始化每个新创建的子通道。
- **客户端**：只需要连接到一个服务端，因此只需要配置一个处理器（`handler`），用于初始化该连接的通道。

### 代码示例说明

#### 服务端

```java
@Slf4j
public class NettyServer {

    public static void main(String[] args) {
        new NettyServer().bind(8080);
    }

    private void bind(int port) {
        // 配置服务端 NIO 线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();  // 处理连接请求的线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();  // 处理 I/O 操作的线程组

        try {
            // 创建服务端启动辅助类，用于简化服务端的通道配置
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)  // 使用 NioServerSocketChannel 实例化一个新的通道对象
             .option(ChannelOption.SO_BACKLOG, 128)  // 配置通道选项
             .childHandler(new MyChannelInitializer());  // 配置子通道的处理器
            
            // 绑定端口，并启动服务器，开始接收连接
            ChannelFuture f = b.bind(port).sync();  // 同步等待绑定完成，确保服务器正确启动
            log.info("server start done");
            
            // 监听通道的关闭事件
            f.channel().closeFuture().sync();  // 同步等待通道关闭，确保服务器运行直到被关闭
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 优雅关闭两个线程组
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
```

#### 客户端

```java
@Slf4j
public class NettyClient {

    public static void main(String[] args) {
        new NettyClient().connect("127.0.0.1", 8080);
    }

    private void connect(String inetHost, int inetPort) {
        // 创建客户端 NIO 线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();  // 处理客户端所有 I/O 事件的线程组

        try {
            // 创建客户端启动辅助类，用于简化客户端的通道配置
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
             .channel(NioSocketChannel.class)  // 使用 NioSocketChannel 实例化一个新的通道对象，以建立连接
             .option(ChannelOption.AUTO_READ, true)  // 配置通道选项，设置自动读取数据
             .handler(new MyChannelInitializer());  // 配置通道的处理器
            
            // 连接指定的主机和端口
            ChannelFuture f = b.connect(inetHost, inetPort).sync();  // 同步等待连接完成，确保客户端正确连接
            log.info("client start done");
            
            // 监听通道的关闭事件
            f.channel().closeFuture().sync();  // 同步等待通道关闭，确保客户端运行直到被关闭
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 优雅关闭线程组
            workerGroup.shutdownGracefully();
        }
    }
}
```

通过这种方式，Netty 能够有效地处理客户端和服务端的不同需求，使它们在各自的职责范围内高效工作。