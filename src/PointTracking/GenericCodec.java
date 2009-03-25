package PointTracking;
import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.format.VideoFormat;

/*********************************************************
	 * A pass-through codec to access to individual frames.
	 *********************************************************/

	public class GenericCodec implements Codec {

		/**
		 * Callback to access individual video frames.
		 */
		void accessFrame(Buffer frame) {

			// For demo, we'll just print out the frame #, time &
			// data length.

			long t = (long) (frame.getTimeStamp() / 10000000f);

			// System.err.println("Pre: frame #: " + frame.getSequenceNumber() +
			// ", time: " + ((float)t)/100f +
			// ", len: " + frame.getLength());
		}

		/**
		 * The code for a pass through codec.
		 */

		// We'll advertize as supporting all video formats.
		protected Format supportedIns[] = new Format[] { new VideoFormat(null) };

		// We'll advertize as supporting all video formats.
		protected Format supportedOuts[] = new Format[] { new VideoFormat(null) };

		Format input = null, output = null;

		public String getName() {
			return "Generic Codec";
		}

		// No op.
		public void open() {
		}

		// No op.
		public void close() {
		}

		// No op.
		public void reset() {
		}

		public Format[] getSupportedInputFormats() {
			return supportedIns;
		}

		public Format[] getSupportedOutputFormats(Format in) {
			if (in == null)
				return supportedOuts;
			else {
				// If an input format is given, we use that input format
				// as the output since we are not modifying the bit stream
				// at all.
				Format outs[] = new Format[1];
				outs[0] = in;
				return outs;
			}
		}

		public Format setInputFormat(Format format) {
			input = format;
			return input;
		}

		public Format setOutputFormat(Format format) {
			output = format;
			return output;
		}

		public int process(Buffer in, Buffer out) {

			// This is the "Callback" to access individual frames.
			accessFrame(in);

			// Swap the data between the input & output.
			Object data = in.getData();
			in.setData(out.getData());
			out.setData(data);

			// Copy the input attributes to the output
			out.setFormat(in.getFormat());
			out.setLength(in.getLength());
			out.setOffset(in.getOffset());

			return BUFFER_PROCESSED_OK;
		}

		public Object[] getControls() {
			return new Object[0];
		}

		public Object getControl(String type) {
			return null;
		}
	}