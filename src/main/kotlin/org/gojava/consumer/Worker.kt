package org.gojava.consumer

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger


class Worker(prefetch:Int, poolExecutor: ThreadPoolExecutor,
             channel:Channel, queue:String, taskCount:AtomicInteger) : DefaultConsumer(channel) {

    val poolExecutor = poolExecutor
    val taskCount = taskCount

    init{
        channel.basicQos( prefetch )
        channel.basicConsume( queue, false, this )
    }

    override fun handleDelivery(consumerTag: String?, envelope: Envelope?, properties: AMQP.BasicProperties?,
                                body: ByteArray?) {

        val task:Runnable = Task(this, envelope!!.deliveryTag, channel, body )
        poolExecutor.execute( task )
    }
}