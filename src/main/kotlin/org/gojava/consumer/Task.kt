package org.gojava.consumer

import com.rabbitmq.client.Channel
import java.util.*

class Task(worker: Worker, tag: Long, channel: Channel, body: ByteArray?) : Runnable {

    val channel: Channel = channel
    val tag: Long = tag
    val worker: Worker = worker
    val body: ByteArray? = body


    override fun run() = try {
        worker.taskCount.incrementAndGet()

        val time: Long = System.currentTimeMillis()
        val threadName: String = Thread.currentThread().name
        var msg: String = "Empty"

        println("$threadName started")

        if (body != null)
            msg = String(body)

        println("Received that msg " + msg)

        //Just to have thread's execution times different
        val r: Random = Random()
        Thread.sleep((r.nextInt(5_000 - 10) + 10).toLong())

        if (channel.isOpen)
            channel.basicAck(tag, false)

        val endTime: Long = System.currentTimeMillis() - time
        println("$threadName finished in $endTime")
    } catch(ex: Exception) {
        when (ex) {
            is InterruptedException -> {
                throw RuntimeException()
            }
            else -> throw ex
        }
    } finally {
        worker.taskCount.decrementAndGet()
    }

}