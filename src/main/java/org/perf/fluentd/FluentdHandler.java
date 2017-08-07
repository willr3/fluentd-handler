package org.perf.fluentd;

import org.fluentd.logger.FluentLogger;
import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;

public class FluentdHandler extends ExtHandler {

    private static FluentLogger LOG = null;

    private String prefix="";
    private String host="";
    private int port=24224;
    private String tag="";
    private boolean messageMap=false;

    /** {@inheritDoc} */
    protected void doPublish(final ExtLogRecord record) {
        if(!isLoggable(record)){
            return;
        }
        //what year is this?
        if(LOG==null){
            synchronized (this){
                if(LOG==null){
                    LOG=FluentLogger.getLogger(prefix,host,port);
                }
            }
        }
        if(isMessageMap()){
            Map<String,Object> data = new HashMap<>();
            data.put("level",record.getLevel());
            data.put("logger", record.getLoggerName());
            data.put("threadName",record.getThreadName());
            data.put("threadID",record.getThreadID());
            data.put("message",record.getFormattedMessage());
            if(record.getThrown()!=null){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintWriter pw = new PrintWriter(baos);
                record.getThrown().printStackTrace(pw);
                data.put("thrown",baos.toString());
                pw.close();
                try {
                    baos.close();
                } catch (IOException e) {
                    reportError("Formatting error", e, ErrorManager.FORMAT_FAILURE);
                }
            }
            LOG.log(getTag(),data,record.getMillis());
        }else{
            String formatted;
            Formatter formatter = getFormatter();
            try {
                formatted = formatter.format(record);
            } catch (Exception ex) {
                reportError("Formatting error", ex, ErrorManager.FORMAT_FAILURE);
                return;
            }
            if (formatted.length() == 0) {
                // nothing to write; don't bother
                return;
            }
            LOG.log(getTag(), "message",formatted,record.getMillis());
        }
        if(isAutoFlush()) {
            LOG.flush(); // probably don't want to flush each message...
        }
    }

    @Override
    public void flush(){
        if(LOG!=null){
            LOG.flush();
        }
        super.flush();
    }

    @Override
    public void close() throws SecurityException {
        if(LOG!=null){
            LOG.close();
        }
        super.close();
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isMessageMap() {
        return messageMap;
    }

    public void setMessageMap(boolean messageMap) {
        this.messageMap = messageMap;
    }
}
