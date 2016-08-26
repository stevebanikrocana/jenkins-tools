// Install as $JENKINS_HOME/init.groovy.d/extralogging.groovy

import java.util.logging.ConsoleHandler
import java.util.logging.FileHandler
import java.util.logging.Formatter
import java.util.logging.LogManager
import java.util.logging.Logger
import java.util.logging.Level
import java.util.logging.SimpleFormatter
import java.text.MessageFormat

//def logger = LogManager.getLogManager().getLogger("hudson.WebAppMain")
def logger = LogManager.getLogManager().getLogger("") //Get highest level logger


//private static FileHandler fh = new FileHandler("mylog.txt");



import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LogFormatter extends Formatter {
  private static final String LINE_SEPARATOR = System.getProperty("line.separator")

  @Override
  public String format(LogRecord record) {
    StringBuilder sb = new StringBuilder()

    sb.append(new Date(record.getMillis()))
      .append(" ")
      .append(record.getLevel().getLocalizedName())
      .append(": ")
      .append(formatMessage(record))
      .append(LINE_SEPARATOR);

    if (record.getThrown() != null) {
      try {
        StringWriter sw = new StringWriter()
        PrintWriter pw = new PrintWriter(sw)
        record.getThrown().printStackTrace(pw)
        pw.close()
        sb.append(sw.toString())
      } catch (Exception ex) {
        // ignore
      }
    }

    return sb.toString()
  }
}

//http://stackoverflow.com/questions/194765/how-do-i-get-java-logging-output-to-appear-on-a-single-line
public class SingleLineFormatter extends Formatter {

  Date dat = new Date();
  private final static String format = "{0,date,long} {0,time,long}";
  private MessageFormat formatter;
  Object[] args = new Object[1]

  private static final String LINE_SEPARATOR = System.getProperty("line.separator")

  /**
   * Format the given LogRecord.
   * @param record the log record to be formatted.
   * @return a formatted log record
   */
  public synchronized String format(LogRecord record) {

    StringBuilder sb = new StringBuilder();

    // Minimize memory allocations here.
    dat.setTime(record.getMillis());    
    args[0] = dat;


    // Date and time 
    StringBuffer text = new StringBuffer();
    if (formatter == null) {
      formatter = new MessageFormat(format);
    }
    formatter.format(args, text, null);
    sb.append(text);
    sb.append(" ");


    // Class name 
    if (record.getSourceClassName() != null) {
      sb.append(record.getSourceClassName());
    } else {
      sb.append(record.getLoggerName());
    }

    // Method name 
    if (record.getSourceMethodName() != null) {
      sb.append(" ");
      sb.append(record.getSourceMethodName());
    }
    sb.append(" - "); // lineSeparator



    String message = formatMessage(record);

    // Level
    sb.append(record.getLevel().getLocalizedName());
    sb.append(": ");

    // Indent - the more serious, the more indented.
    //sb.append( String.format("% ""s") );
    int iOffset = (1000 - record.getLevel().intValue()) / 100;
    for( int i = 0; i < iOffset;  i++ ){
      sb.append(" ");
    }


    sb.append(message);
    sb.append(LINE_SEPARATOR);
    if (record.getThrown() != null) {
      try {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        record.getThrown().printStackTrace(pw);
        pw.close();
        sb.append(sw.toString());
      } catch (Exception ex) {
      }
    }
    return sb.toString();
  }
}


logger.info("before adding new handler")

FileHandler handler = new FileHandler("/var/log/jenkins/jenkins_formatted.log")

//Formatter formatter = new SimpleFormatter()
//Formatter formatter = new LogFormatter()
Formatter formatter = new SingleLineFormatter()

handler.formatter = formatter

//logger.addHandler (new ConsoleHandler())
//logger.addHandler (new FileHandler("/tmp/jenkins.log"))
logger.addHandler (handler)

logger.info("after adding new handler")

logger.severe("test severe log message")

logger.finest("test finest log message")


