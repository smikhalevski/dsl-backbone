/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.extenders;

import org.ehony.dsl.ContainerBaseTag;
import org.ehony.dsl.api.ContainerTag;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;
import java.io.*;
import java.nio.charset.*;

import static org.ehony.dsl.util.Validate.notNull;

/**
 * Abstract tag-based mixin support of optional <code>encoding</code> attribute.
 * <p>Provides basic serialization mechanisms for arbitrary objects.</p>
 * <p>By default instance with default system charset (usually UTF-8) is built.</p>
 * 
 * @param <Type> builder type returned by fluent methods.
 * @param <Parent> type of optional parent object.
 * @see Charset#defaultCharset()
 */
@XmlTransient
public class EncodingBaseTag<
        Type extends EncodingBaseTag<Type, Parent>,
        Parent extends ContainerTag
        >
        extends ContainerBaseTag<Type, Parent>
{

    @XmlTransient
    private Charset charset = Charset.defaultCharset();

    /**
     * Get name of encoding in use.
     * @return The canonical name of charset exploited under the hood.
     */
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public String getEncoding() {
        return charset.name();
    }

    /**
     * Set name of encoding.
     * 
     * @param encoding encoding name.
     * @exception UnsupportedCharsetException if no support for the named
     *            encoding is available in this instance of the Java virtual machine.
     */
    public void setEncoding(String encoding) {
        this.charset = Charset.forName(encoding);
    }

    /**
     * Get charset strings are encoded with.
     * @return Instance of {@link Charset}.
     */
    @XmlTransient
    public Charset getCharset() {
        return charset;
    }

    /**
     * Set charset to encode strings with.
     * @param charset another charset.
     */
    public void setCharset(Charset charset) {
        notNull(charset, "Charset expected.");
        this.charset = charset;
    }

    /**
     * Type aware object serialization to byte array.
     * <p>Byte arrays are returned as is, strings are encoded with current charset
     * and objects are serialised iff implement {@link Serializable} interface.</p>
     * 
     * @param obj source object to serialize.
     * @return Array of bytes representing object.
     * @throws IllegalArgumentException object cannot be serialised.
     */
    public byte[] encode(Object obj) {
        if (obj instanceof byte[]) {
            return (byte[]) obj;
        }
        if (obj instanceof String) {
            return charset.encode(obj.toString()).array();
        }
        if (obj instanceof Serializable) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream(512);
            
            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(bytes);
                out.writeObject(obj);
                return bytes.toByteArray();
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot serialize " + obj, e);
            } finally {
                try {
                    out.close();
                } catch (Exception ex) {
                    // Quiet
                }
            }
        }
        throw new IllegalArgumentException("Serializable expected but " + obj + " found.");
    }
    
    // <editor-fold desc="Fluent API">

    /**
     * Set charset to encode strings with.
     * 
     * @param charset another charset.
     * @return Original builder instance.
     */
    @SuppressWarnings("unchecked")
    public Type charset(Charset charset) {
        setCharset(charset);
        return (Type) this;
    }

    /**
     * Set name of encoding.
     * 
     * @param encoding name of the encoding to set.
     * @return Original builder instance.
     */
    @SuppressWarnings("unchecked")
    public Type encoding(String encoding) {
        setEncoding(encoding);
        return (Type) this;
    }

    /**
     * Set windows-1251 encoding.
     * @return Original builder instance.
     */
    public Type cp1251() {
        return encoding("cp1251");
    }

    /**
     * Set utf-8 encoding.
     * @return Original builder instance.
     */
    public Type utf8() {
        return encoding("utf-8");
    }

    // </editor-fold>    

    // <editor-fold desc="Debug">

    @Override
    protected String getDebugInfo() {
        return super.getDebugInfo() + "\nencoding = " + getEncoding();
    }

    // <editor-fold>
}
