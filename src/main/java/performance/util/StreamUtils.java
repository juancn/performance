package performance.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
    private static final int BUF_SIZE = 8192;

    public static byte[] readBytes(final InputStream is)
        throws IOException
    {
        final ByteArrayOutputStream out;

        try {
            out = new ByteArrayOutputStream();
            copy(is, out);

            is.close();
        }
        finally {
            close(is);
        }

        return out.toByteArray();
    }

    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            //Ignore
        }
    }

    public static long copy(InputStream is, OutputStream os)
        throws IOException
    {
        final byte[] buff = new byte[BUF_SIZE];
        long         transferred = 0;

        IOException exception = null;
        int         len = 0, nRead;

        do {
            try {
                nRead = is.read(buff, len, BUF_SIZE - len);
            }
            catch (IOException e) {
                // In case of exception, write buffer first, then re-raise
                exception = e;
                nRead = -1;
            }

            // Write buffer at EOF or when at least half buffer is full
            if (nRead == -1 ? len > 0 : (len += nRead) > BUF_SIZE / 2) {
                os.write(buff, 0, len);
                transferred += len;
                len = 0;
            }
        }
        while (nRead != -1);

        if (exception != null) {
            throw exception;
        }

        return transferred;
    }


}
