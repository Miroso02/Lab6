import java.awt.Font
import java.awt.event.ActionEvent
import java.io.*
import java.lang.NumberFormatException
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JOptionPane
import kotlin.system.exitProcess

var object1 : Socket = Socket()
var object2 : Socket = Socket()
val ss1 = ServerSocket(6666)
val ss2 = ServerSocket(6667)
var t1 = Thread(Runnable { object1 = ss1.accept() })
var t2 = Thread(Runnable { object2 = ss2.accept() })

val editXmin = HintTextField("xMin")
val editYmin = HintTextField("yMin")
val editXmax = HintTextField("xMax")
val editYmax = HintTextField("yMax")
val editNPoint = HintTextField("nPoint")
val frame = JFrame("Main")
fun main()
{
    initFrame()
    t1.start()
    t2.start()
}

var bw1 = BufferedWriter(Writer.nullWriter())
var br1 = BufferedReader(Reader.nullReader())
var bw2 = BufferedWriter(Writer.nullWriter())
fun sending1(e: ActionEvent)
{
        val userInput = getOutput() ?: return
        checkConnections()
        bw1 = BufferedWriter(OutputStreamWriter(object1.getOutputStream()))
        br1 = BufferedReader(InputStreamReader(object1.getInputStream()))
        bw2 = BufferedWriter(OutputStreamWriter(object2.getOutputStream()))

    try
    {
        userInput.forEach { bw1.write("$it\n") }
        bw1.flush()
    }
    catch (e: SocketException)
    {
        JOptionPane.showMessageDialog(frame,
                "Помилка." +
                        "\nОдна з додаткових програм (1) була закрита після запуску." +
                        "\nСпробуйте ще раз")
        t1 = Thread(Runnable { object1 = ss1.accept() })
        t1.start()
        return
    }

    try
    {
        bw2.write("${userInput[0]}\n")
        for (i in 0 until 2 * userInput[0])
            bw2.write(br1.readLine() + "\n")
        bw2.flush()
    }
    catch (e: SocketException)
    {
        JOptionPane.showMessageDialog(frame,
                "Помилка." +
                        "\nОдна з додаткових програм (2) була закрита після запуску." +
                        "\nСпробуйте ще раз")
        t2 = Thread(Runnable { object2 = ss2.accept() })
        t2.start()
        return
    }
}

fun checkConnections()
{
    if (t1.isAlive)
    {
        try
        {
            Runtime.getRuntime().exec("./Object1.exe")
        }
        catch (e: IOException) {e.printStackTrace(); exitProcess(0)}
        t1.join()
    }

    if (t2.isAlive)
    {
        try
        {
            Runtime.getRuntime().exec("./Object2.exe")
        }
        catch (e: IOException) {e.printStackTrace(); exitProcess(0)}
        t2.join()
    }
}

fun getOutput() : Array<Int>?
{
    val data = Array(5) { 0 }
    var errMessage = "При вводі використані неправильні символи"
    try
    {
        data[0] = Integer.parseInt(editNPoint.text)
        data[1] = Integer.parseInt(editXmin.text)
        data[2] = Integer.parseInt(editYmin.text)
        data[3] = Integer.parseInt(editXmax.text)
        data[4] = Integer.parseInt(editYmax.text)
        errMessage = when {
            data[3] < data[1] -> "xMin має бути меншим за xMax"
            data[4] < data[2] -> "yMin має бути меншим за yMax"
            data[0] <= 0 -> "кількість точок має бути > 0"
            else -> errMessage
        }
        if (data[3] <= data[1] || data[4] <= data[2] || data[0] <= 0)
            throw NumberFormatException(errMessage)
    }
    catch (e: NumberFormatException)
    {
        JOptionPane.showMessageDialog(frame, "Неправильні вхідні дані:\n$errMessage")
        return null
    }
    return data
}

fun initFrame()
{
    val button = JButton("Відобразити графік")
    editNPoint.setBounds(160, 70, 80, 20)
    editXmin.setBounds(110, 110, 80, 20)
    editYmin.setBounds(210, 110, 80, 20)
    editXmax.setBounds(110, 130, 80, 20)
    editYmax.setBounds(210, 130, 80, 20)
    button.setBounds(120, 200, 160, 40)
    button.addActionListener(::sending1)

    val l1 = JLabel("Введіть дані")
    l1.font = Font("Arial", Font.PLAIN, 20)
    l1.setBounds(140, 10, 120, 30)
    val l2 = JLabel("x")
    l2.setBounds(150, 90, 20, 20)
    val l3 = JLabel("y")
    l3.setBounds(250, 90, 20, 20)
    val l4 = JLabel("Кількість точок")
    l4.setBounds(150, 50, 100, 20)
    val l21 = JLabel("min")
    l21.setBounds(80, 110, 25, 20)
    val l22 = JLabel("max")
    l22.setBounds(80, 130, 25, 20)
    val l31 = JLabel("min")
    l31.setBounds(300, 110, 25, 20)
    val l32 = JLabel("max")
    l32.setBounds(300, 130, 25, 20)
    frame.add(l1); frame.add(l2); frame.add(l3); frame.add(l4); frame.add(l21); frame.add(l22); frame.add(l31); frame.add(l32)

    frame.add(editXmin); frame.add(editYmin); frame.add(editXmax); frame.add(editYmax); frame.add(editNPoint)
    frame.add(button)
    frame.layout = null
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(400,400)
    frame.isVisible = true
}