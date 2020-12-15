import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ConnectException
import java.net.Socket
import java.net.SocketException
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.system.exitProcess

import java.lang.Integer.parseInt as int

var socket = Socket()
var nPoints = 0
var startXPoints = emptyArray<Int>()
var startYPoints = emptyArray<Int>()
var xPoints = emptyArray<Int>()
var yPoints = emptyArray<Int>()
val frame = JFrame("Object2 (Очікування підключення...)")
fun main()
{
    frame.add(graph)
    graph.isVisible = false
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(600,600)
    frame.isVisible = true
    while (!socket.isConnected)
    {
        try
        {
            Thread.sleep(1000)
            socket = Socket("localhost", 6667)
        }
        catch (e: ConnectException) {}
    }
    frame.title = "Object2 (Підключено)"
    val br = BufferedReader(InputStreamReader(socket.getInputStream()))
    while (true)
    {
        getData(br)
        drawGraph()
    }
}

fun getData(br: BufferedReader)
{
    try {
        nPoints = int(br.readLine())
        xPoints = Array(nPoints) { 0 }
        yPoints = Array(nPoints) { 0 }
        for (i in 0 until nPoints)
            xPoints[i] = int(br.readLine())
        for (i in 0 until nPoints)
            yPoints[i] = int(br.readLine())
    } catch (e: SocketException) {
        exitProcess(0)
    }
}

val graph = object : JPanel()
{
    init
    {
        background = Color.PINK
    }
    override fun paintComponent(g: Graphics?)
    {
        if (g == null) return
        g.color = Color.BLACK
        g.drawLine(0, frame.height - 60, frame.width, frame.height - 60)
        g.fillPolygon(intArrayOf(frame.width - 15, frame.width - 25, frame.width - 25),
                    intArrayOf(frame.height - 60, frame.height - 55, frame.height - 65), 3)
        g.drawString("x", frame.width - 25, frame.height - 45)
        g.drawLine(20, 0, 20, frame.height)
        g.fillPolygon(intArrayOf(20, 25, 15),
                intArrayOf(0, 10, 10), 3)
        g.drawString("y", 10, 10)
        g.drawPolyline(xPoints.toIntArray(), yPoints.toIntArray(), nPoints)

        g.font = Font("Arial", Font.ITALIC, 10)
        for (i in 0 until nPoints)
        {
            g.drawString("${startXPoints[i]}", xPoints[i] - 5, frame.height - 50)
            g.drawString("${startYPoints[i]}", 10, yPoints[i] + 3)
        }
    }
}
fun drawGraph()
{
    val xMin = xPoints.min() ?: 0
    val yMin = yPoints.min() ?: 0
    val sorting = Array(nPoints) { Array(2) {0} }
    sorting.forEachIndexed { i, _ -> sorting[i][0] = xPoints[i]; sorting[i][1] = yPoints[i] }
    sorting.sortBy { it[0] }
    xPoints.forEachIndexed { i, _ -> xPoints[i] = sorting[i][0]}
    yPoints.forEachIndexed { i, _ -> yPoints[i] = sorting[i][1]}
    startXPoints = xPoints.copyOf()
    startYPoints = yPoints.copyOf()
    xPoints.forEachIndexed { i, _ -> xPoints[i] = (xPoints[i] - xMin + 1) * 10 + 50 }
    yPoints.forEachIndexed { i, _ -> yPoints[i] = frame.height - 50 - (yPoints[i] - yMin + 1) * 10 }
    graph.isVisible = true
    frame.repaint()
}
