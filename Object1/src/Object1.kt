import java.io.*
import java.net.ConnectException
import java.net.Socket
import java.net.SocketException
import javax.swing.*
import kotlin.random.Random
import kotlin.system.exitProcess

import java.lang.Integer.parseInt as int

var socket = Socket()
var oldPane = JScrollPane()

var nPoint = 0; var xMin = 0; var yMin = 0; var xMax = 0; var yMax = 0
var table = JTable()

fun main()
{
    val frame = JFrame("Object1 (Очікування підключення...)")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(600,600)
    frame.isVisible = true
    while (!socket.isConnected)
    {
        try
        {
            Thread.sleep(1000)
            socket = Socket("localhost", 6666)
        }
        catch (e: ConnectException) {}
    }
    frame.title = "Object1 (Підключено)"
    val br = BufferedReader(InputStreamReader(socket.getInputStream()))
    val bw = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
    while (true)
    {
        getData(br)
        val xValues = List(nPoint) { Random.nextInt(xMin, xMax) }
        val yValues = List(nPoint) { Random.nextInt(yMin, yMax) }

        val columns = arrayOf("x", "y")
        val values = Array(nPoint) { arrayOf(xValues[it], yValues[it]) }
        table = FixedTable(values, columns)
        frame.remove(oldPane)
        oldPane = JScrollPane(table)
        frame.add(oldPane)
        frame.validate()

        xValues.forEach { bw.write("$it\n") }
        yValues.forEach { bw.write("$it\n") }
        bw.flush()
    }
}

fun getData(br: BufferedReader)
{
    try
    {
        nPoint = int(br.readLine())
        xMin = int(br.readLine())
        yMin = int(br.readLine())
        xMax = int(br.readLine())
        yMax = int(br.readLine())
    }
    catch (e: SocketException)
    {
        exitProcess(0)
    }
}

class FixedTable(rowData: Array<Array<Int>>, columns: Array<String>) : JTable(rowData, columns)
{
    override fun isCellEditable(row: Int, column: Int) = false
}
