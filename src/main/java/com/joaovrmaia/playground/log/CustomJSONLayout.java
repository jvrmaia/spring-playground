package com.joaovrmaia.playground.log;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.message.Message;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


@Plugin(name = "CustomJSONLayout", category = "Core", elementType = "layout", printObject = true)
public class CustomJSONLayout extends AbstractStringLayout {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    private final JsonFactory jsonFactory = new JsonFactory();

    protected CustomJSONLayout(final Charset charset) {
        super(charset);
    }

    private static void writeBasicFields(final LogEvent event, final JsonGenerator g)
            throws IOException {
        g.writeStringField("logger", event.getLoggerName());
        g.writeStringField("level", event.getLevel().toString());
        g.writeNumberField("timestamp", event.getTimeMillis());
        g.writeStringField("threadName", event.getThreadName());
    }

    private static void writeMessageField(final LogEvent event,
                                          final JsonGenerator g) throws IOException {

        final Message message = event.getMessage();
        if (message == null) return;

        if (message instanceof MapMessage) {
            final MapMessage mapMessage = (MapMessage) message;
            final Map<String, String> map = mapMessage.getData();
            writeStringMap(map, g);
        } else {
            g.writeStringField("message", message.toString());
        }
    }

    private static void writeExtraFields(final LogEvent event, final JsonGenerator g) throws IOException {
        writeStringMap(event.getContextData().toMap(), g);
    }

    private static void writeStringMap(final Map<String, String> stringMap, final JsonGenerator g) throws IOException {
        if (stringMap == null || stringMap.isEmpty()) return;
        final Set<String> keys = new TreeSet<String>(stringMap.keySet());
        for (final String key : keys) {
            g.writeStringField(key, stringMap.get(key));
        }
    }

    private static void writeThrowableEvents(final LogEvent event,
                                             final JsonGenerator g) throws IOException {
        final Throwable thrown = event.getThrown();
        if (thrown == null)
            return;

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        thrown.printStackTrace(pw);

        String throwableString = sw.toString();
        if (throwableString.isEmpty())
            return;

        g.writeStringField("throwable", throwableString);
    }

    @PluginFactory
    public static CustomJSONLayout createLayout(
            @PluginAttribute("charset") final String charset) {
        Charset c = UTF8;
        if (charset != null) {
            if (Charset.isSupported(charset)) {
                c = Charset.forName(charset);
            } else {
                LOGGER.error("Charset " + charset
                        + " is not supported for layout, using " + c.displayName());
            }
        }
        return new CustomJSONLayout(c);
    }

    @Override
    public String toSerializable(final LogEvent event) {
        try {
            final StringWriter stringWriter = new StringWriter();
            final JsonGenerator g = jsonFactory.createJsonGenerator(stringWriter);
            g.writeStartObject();
            writeBasicFields(event, g);
            writeMessageField(event, g);
            writeExtraFields(event, g);
            writeThrowableEvents(event, g);
            g.writeEndObject();
            g.close();
            stringWriter.append("\n");
            return stringWriter.toString();
        } catch (IOException e) {
            LOGGER.error("Could not write event as JSON", e);
        }
        return StringUtils.EMPTY;
    }
}