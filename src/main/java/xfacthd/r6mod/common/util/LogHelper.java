package xfacthd.r6mod.common.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class LogHelper
{
    private static Logger logger;

    private static void log(Level logLevel, Object object, Object... data)
    {
        logger.log(logLevel, String.valueOf(object), object, data);
    }

    public static void all(Object object, Object... data) { log(Level.ALL, object, data); }

    public static void debug(Object object, Object... data) { log(Level.DEBUG, object, data); }

    public static void error(Object object, Object... data) { log(Level.ERROR, object, data); }

    public static void fatal(Object object, Object... data) { log(Level.FATAL, object, data); }

    public static void info(Object object, Object... data) { log(Level.INFO, object, data); }

    public static void off(Object object, Object... data) { log(Level.OFF, object, data); }

    public static void trace(Object object, Object... data) { log(Level.TRACE, object, data); }

    public static void warn(Object object, Object... data) { log(Level.WARN, object, data); }

    public static void setLogger(Logger log) { logger = log; }
}