import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.JTextField

class HintTextField(private val hint: String) : JTextField(hint), FocusListener
{
    private var showHint : Boolean
    override fun getText(): String = if (showHint) "" else super.getText()

    init
    {
        this.showHint = true
        addFocusListener(this)
    }
    override fun focusLost(e: FocusEvent?)
    {
        if (this.text.isEmpty())
        {
            text = hint
            showHint = true
        }
    }
    override fun focusGained(e: FocusEvent?)
    {
        if (this.text.isEmpty())
        {
            text = ""
            showHint = false
        }
    }
}