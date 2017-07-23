package org.gojava.consumer

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

@Component
class Runner : CommandLineRunner {

    val queue:String = "hello"

    override fun run(args:Array<String>) {
        val channel = connectChannel()

        val threadFactory: ThreadFactory = Executors.defaultThreadFactory()
        val poolExecutor: ThreadPoolExecutor = ThreadPoolExecutor(5, 10, 10_000,
                TimeUnit.SECONDS, ArrayBlockingQueue<Runnable>(2), threadFactory)

        Worker(1, poolExecutor,  channel, queue, taskCount )
    }

    fun connectChannel() : Channel {
        val factory: ConnectionFactory = ConnectionFactory()
        factory.host = "localhost"

        val connection: Connection = factory.newConnection()
        val channel: Channel = connection.createChannel()

        channel.queueDeclare(queue, false, false, false, null)

        return channel
    }
}