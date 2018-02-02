package com.ec.common.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;




 
public class FileUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);


	/**
	 * 閸愭瑥鍙嗛弬鍥︽
	 * 
	 * @param fileName
	 * @param is
	 * @throws IOException
	 */
	public static void write(String filename, InputStream is)
			throws IOException {
		FileOutputStream fos=null;
		try{
			fos = new FileOutputStream(filename);
			byte[] bs = new byte[1024];
			int len = 0;
			while ((len = is.read(bs)) != -1) {
				fos.write(bs, 0, len);
			}
		}finally {
			
			close(is);
			close(fos);
			
		}
		
	}

	public static void write(String filename, OutputStream out)
			throws IOException {
		FileInputStream in = null;
		try {
			in = new FileInputStream(filename);
			byte[] bs = new byte[512];
			int len = 0;
			while ((len = in.read(bs)) > 0) {
				out.write(bs, 0, len);
			}
		} finally {
			close(in);
			close(out);
		}
	}

	 
	
	/**
	 * 閸掋倖鏌囬弬鍥︽閺勵垰鎯佺�涙ê婀�
	 * @param dir
	 * @return
	 */
	public static boolean isExist(String dir){
		boolean isExist=false;
		File fileDir=new File(dir);
		if(fileDir.isDirectory()){
			File[] files=fileDir.listFiles();
			if(files!=null&&files.length!=0){
				isExist=true;
			}
		}
		return isExist;
	}
	

	/**
	 * 閼惧嘲褰囬弬鍥︽閻ㄥ嫬鐡х粭锕傛肠
	 * 
	 * @param file
	 * @return
	 */
	public static String getCharset(File file) {
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		BufferedInputStream bis=null;
		try {
			boolean checked = false;
			bis = new BufferedInputStream(
					new FileInputStream(file));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1)
				return charset;
			if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE
					&& first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF
					&& first3Bytes[1] == (byte) 0xBB
					&& first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8";
				checked = true;
			}
			bis.reset();

			if (!checked) {
				int loc = 0;
				while ((read = bis.read()) != -1) {
					loc++;
					if (read >= 0xF0)
						break;
					// 閸楁洜瀚崙铏瑰箛BF娴犮儰绗呴惃鍕剁礉娑旂喓鐣婚弰鐤揃K
					if (0x80 <= read && read <= 0xBF)
						break;
					if (0xC0 <= read && read <= 0xDF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF)// 閸欏苯鐡ч懞锟� (0xC0 - 0xDF)
							// (0x80 -
							// 0xBF),娑旂喎褰查懗钘夋躬GB缂傛牜鐖滈崘锟�
							continue;
						else
							break;
						// 娑旂喐婀侀崣顖濆厴閸戞椽鏁婇敍灞肩稻閺勵垰鍤戦悳鍥窛鐏忥拷
					} else if (0xE0 <= read && read <= 0xEF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) {
							read = bis.read();
							if (0x80 <= read && read <= 0xBF) {
								charset = "UTF-8";
								break;
							} else
								break;
						} else
							break;
					}
				}

			}
			
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(),e);
		}finally {
			close(bis);
		}
		return charset;
	}


	/**
	 * 閸掓稑缂撻弬鍥︽閻╊喖缍�
	 * 
	 * @param dirstr
	 *            閺嶅湱娲拌ぐ锟�
	 * @param name
	 *            鐎涙劗娲拌ぐ鏇炴倳缁夛拷
	 * @return
	 */
	
	
	
	
	
	
	/**
	 * 閸掔娀娅庨弬鍥︽
	 * 
	 * @param path
	 * @return
	 */
	public static boolean delete(String path) {
		File file = new File(path);
		return file.delete();
	}

	/**
	 * 閸掔娀娅庨惄顔肩秿
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteLeaves(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteLeaves(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	
 

	/**
	 * 閺嶈宓侀弬鍥︽鐠侯垰绶為崚娑樼紦閺傚洣娆㈡径锟�,婵″倹鐏夌捄顖氱窞娑撳秴鐡ㄩ崷銊ュ灟閸掓稑缂�.
	 * 
	 * @param path
	 */
	public static void create(String path) {
		create(path, true);
	}

	/**
	 * 閸掓稑缂撻弬鍥︽婢讹拷
	 * 
	 * @param path
	 * @param isFile
	 */
	public static void create(String path, boolean isFile) {
		if (isFile) {
			path = path.substring(0, path.lastIndexOf(File.separator));
		}
		File file = new File(path);
		if (!file.exists())
			file.mkdirs();
	}

	/**
	 * 閸掓稑缂撻弬鍥︽閻╊喖缍�
	 * 
	 * @param dirstr
	 *            閺嶅湱娲拌ぐ锟�
	 * @param name
	 *            鐎涙劗娲拌ぐ鏇炴倳缁夛拷
	 * @return
	 */

 

	 

	 
	/**
	 * 閸欐牕绶遍弬鍥︽閹碘晛鐫嶉崥锟�
	 * 
	 * @return
	 */
	public static String getExt(File file) {
		if (file.isFile()) {
			return getExt(file.getName());
		}
		return "";
	}
	
	/**
	 * 閺嶈宓侀弬鍥︽閸氬秷骞忛崣鏍ㄥ⒖鐏炴洖鎮曠粔鑸拷锟�
	 * @param fileName
	 * @return
	 */
	public static String getExt(String fileName){
		int pos=fileName.lastIndexOf(".");
		if(pos>-1){
			return fileName.substring(pos + 1).toLowerCase();
		}
		return "";
	}
	
	
 

	public static ArrayList<File> getLeaves(String pathname) {
		ArrayList<File> fileList = new ArrayList<File>();
		File[] fileArr = new File(pathname).listFiles();
		for(File f:fileArr){
			if(f.isFile()){
				fileList.add(f);
			}else{
				fileList.addAll(getLeaves(f.getPath()));
			}
		}
		return fileList;
	}
	
	
	

	/**
	 * Stream the given input to the given output via NIO {@link Channels} and a
	 * directly allocated NIO {@link ByteBuffer}. Both the input and output
	 * streams will implicitly be closed after streaming, regardless of whether
	 * an exception is been thrown or not.
	 * 
	 * @param input
	 *            The input stream.
	 * @param output
	 *            The output stream.
	 * @return The length of the written bytes.
	 * @throws IOException
	 *             When an I/O error occurs.
	 */
	private static final int DEFAULT_STREAM_BUFFER_SIZE = 10240;
	public static long write(InputStream input, OutputStream output) throws IOException {
		try (ReadableByteChannel inputChannel = Channels.newChannel(input);
				WritableByteChannel outputChannel = Channels.newChannel(output)) {
			ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_STREAM_BUFFER_SIZE);
			long size = 0;

			while (inputChannel.read(buffer) != -1) {
				buffer.flip();
				size += outputChannel.write(buffer);
				buffer.clear();
			}

			return size;
		}
	}

	/**
	 * Stream a specified range of the given file to the given output via NIO
	 * {@link Channels} and a directly allocated NIO {@link ByteBuffer}. The
	 * output stream will only implicitly be closed after streaming when the
	 * specified range represents the whole file, regardless of whether an
	 * exception is been thrown or not.
	 * 
	 * @param file
	 *            The file.
	 * @param output
	 *            The output stream.
	 * @param start
	 *            The start position (offset).
	 * @param length
	 *            The (intented) length of written bytes.
	 * @return The (actual) length of the written bytes. This may be smaller
	 *         when the given length is too large.
	 * @throws IOException
	 *             When an I/O error occurs.
	 * @since 2.2
	 */
	public static long write(File file, OutputStream output, long start, long length) throws IOException {
		if (start == 0 && length >= file.length()) {
			return write(new FileInputStream(file), output);
		}

		try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(file.toPath(), StandardOpenOption.READ)) {
			WritableByteChannel outputChannel = Channels.newChannel(output);
			ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_STREAM_BUFFER_SIZE);
			long size = 0;

			while (fileChannel.read(buffer, start + size) != -1) {
				buffer.flip();

				if (size + buffer.limit() > length) {
					buffer.limit((int) (length - size));
				}

				size += outputChannel.write(buffer);

				if (size >= length) {
					break;
				}

				buffer.clear();
			}

			return size;
		}
	}
	
	public static IOException close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			}
			catch (IOException e) {
				return e;
			}
		}

		return null;
	}

	/**
	 * Read the given input stream into a byte array. The given input stream
	 * will implicitly be closed after streaming, regardless of whether an
	 * exception is been thrown or not.
	 * 
	 * @param input
	 *            The input stream.
	 * @return The input stream as a byte array.
	 * @throws IOException
	 *             When an I/O error occurs.
	 * @since 2.0
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		write(input, output);
		return output.toByteArray();
	}

		 

 
	public static String probeContentType(String path){
		return contenttype.get(FileUtil.getExt(path).toLowerCase());
	}
	
	private static Map<String,String> contenttype=new HashMap<>();
	static {
		contenttype.put("load"	,	"text/html"	);
		contenttype.put("123"	,	"application/vnd.lotus-1-2-3"	);
		contenttype.put("3ds"	,	"image/x-3ds"	);
		contenttype.put("3g2"	,	"video/3gpp"	);
		contenttype.put("3ga"	,	"video/3gpp"	);
		contenttype.put("3gp"	,	"video/3gpp"	);
		contenttype.put("3gpp"	,	"video/3gpp"	);
		contenttype.put("602"	,	"application/x-t602"	);
		contenttype.put("669"	,	"audio/x-mod"	);
		contenttype.put("7z"	,	"application/x-7z-compressed"	);
		contenttype.put("a"	    ,	"application/x-archive"	);
		contenttype.put("aac"	,	"audio/mp4"	);
		contenttype.put("abw"	,	"application/x-abiword"	);
		contenttype.put("abw.crashed"	,	"application/x-abiword"	);
		contenttype.put("abw.gz"	,	"application/x-abiword"	);
		contenttype.put("ac3"	,	"audio/ac3"	);
		contenttype.put("ace"	,	"application/x-ace"	);
		contenttype.put("adb"	,	"text/x-adasrc"	);
		contenttype.put("ads"	,	"text/x-adasrc"	);
		contenttype.put("afm"	,	"application/x-font-afm"	);
		contenttype.put("ag"	,	"image/x-applix-graphics"	);
		contenttype.put("ai"	,	"application/illustrator"	);
		contenttype.put("aif"	,	"audio/x-aiff"	);
		contenttype.put("aifc"	,	"audio/x-aiff"	);
		contenttype.put("aiff"	,	"audio/x-aiff"	);
		contenttype.put("al"	,	"application/x-perl"	);
		contenttype.put("alz"	,	"application/x-alz"	);
		contenttype.put("amr"	,	"audio/amr"	);
		contenttype.put("ani"	,	"application/x-navi-animation"	);
		contenttype.put("anim[1-9j]"	,	"video/x-anim"	);
		contenttype.put("anx"	,	"application/annodex"	);
		contenttype.put("ape"	,	"audio/x-ape"	);
		contenttype.put("arj"	,	"application/x-arj"	);
		contenttype.put("arw"	,	"image/x-sony-arw"	);
		contenttype.put("as"	,	"application/x-applix-spreadsheet"	);
		contenttype.put("asc"	,	"text/plain"	);
		contenttype.put("asf"	,	"video/x-ms-asf"	);
		contenttype.put("asp"	,	"application/x-asp"	);
		contenttype.put("ass"	,	"text/x-ssa"	);
		contenttype.put("asx"	,	"audio/x-ms-asx"	);
		contenttype.put("atom"	,	"application/atom+xml"	);
		contenttype.put("au"	,	"audio/basic"	);
		contenttype.put("avi"	,	"video/x-msvideo"	);
		contenttype.put("aw"	,	"application/x-applix-word"	);
		contenttype.put("awb"	,	"audio/amr-wb"	);
		contenttype.put("awk"	,	"application/x-awk"	);
		contenttype.put("axa"	,	"audio/annodex"	);
		contenttype.put("axv"	,	"video/annodex"	);
		contenttype.put("bak"	,	"application/x-trash"	);
		contenttype.put("bcpio"	,	"application/x-bcpio"	);
		contenttype.put("bdf"	,	"application/x-font-bdf"	);
		contenttype.put("bib"	,	"text/x-bibtex"	);
		contenttype.put("bin"	,	"application/octet-stream"	);
		contenttype.put("blend"	,	"application/x-blender"	);
		contenttype.put("blender"	,	"application/x-blender"	);
		contenttype.put("bmp"	,	"image/bmp"	);
		contenttype.put("bz"	,	"application/x-bzip"	);
		contenttype.put("bz2"	,	"application/x-bzip"	);
		contenttype.put("c"	,	"text/x-csrc"	);
		contenttype.put("c++"	,	"text/x-c++src"	);
		contenttype.put("cab"	,	"application/vnd.ms-cab-compressed"	);
		contenttype.put("cb7"	,	"application/x-cb7"	);
		contenttype.put("cbr"	,	"application/x-cbr"	);
		contenttype.put("cbt"	,	"application/x-cbt"	);
		contenttype.put("cbz"	,	"application/x-cbz"	);
		contenttype.put("cc"	,	"text/x-c++src"	);
		contenttype.put("cdf"	,	"application/x-netcdf"	);
		contenttype.put("cdr"	,	"application/vnd.corel-draw"	);
		contenttype.put("cer"	,	"application/x-x509-ca-cert"	);
		contenttype.put("cert"	,	"application/x-x509-ca-cert"	);
		contenttype.put("cgm"	,	"image/cgm"	);
		contenttype.put("chm"	,	"application/x-chm"	);
		contenttype.put("chrt"	,	"application/x-kchart"	);
		contenttype.put("class"	,	"application/x-java"	);
		contenttype.put("cls"	,	"text/x-tex"	);
		contenttype.put("cmake"	,	"text/x-cmake"	);
		contenttype.put("cpio"	,	"application/x-cpio"	);
		contenttype.put("cpio.gz"	,	"application/x-cpio-compressed"	);
		contenttype.put("cpp"	,	"text/x-c++src"	);
		contenttype.put("cr2"	,	"image/x-canon-cr2"	);
		contenttype.put("crt"	,	"application/x-x509-ca-cert"	);
		contenttype.put("crw"	,	"image/x-canon-crw"	);
		contenttype.put("cs"	,	"text/x-csharp"	);
		contenttype.put("csh"	,	"application/x-csh"	);
		contenttype.put("css"	,	"text/css"	);
		contenttype.put("cssl"	,	"text/css"	);
		contenttype.put("csv"	,	"text/csv"	);
		contenttype.put("cue"	,	"application/x-cue"	);
		contenttype.put("cur"	,	"image/x-win-bitmap"	);
		contenttype.put("cxx"	,	"text/x-c++src"	);
		contenttype.put("d"	,	"text/x-dsrc"	);
		contenttype.put("dar"	,	"application/x-dar"	);
		contenttype.put("dbf"	,	"application/x-dbf"	);
		contenttype.put("dc"	,	"application/x-dc-rom"	);
		contenttype.put("dcl"	,	"text/x-dcl"	);
		contenttype.put("dcm"	,	"application/dicom"	);
		contenttype.put("dcr"	,	"image/x-kodak-dcr"	);
		contenttype.put("dds"	,	"image/x-dds"	);
		contenttype.put("deb"	,	"application/x-deb"	);
		contenttype.put("der"	,	"application/x-x509-ca-cert"	);
		contenttype.put("desktop"	,	"application/x-desktop"	);
		contenttype.put("dia"	,	"application/x-dia-diagram"	);
		contenttype.put("diff"	,	"text/x-patch"	);
		contenttype.put("divx"	,	"video/x-msvideo"	);
		contenttype.put("djv"	,	"image/vnd.djvu"	);
		contenttype.put("djvu"	,	"image/vnd.djvu"	);
		contenttype.put("dng"	,	"image/x-adobe-dng"	);
		contenttype.put("doc"	,	"application/msword"	);
		contenttype.put("docbook"	,	"application/docbook+xml"	);
		contenttype.put("docm"	,	"application/vnd.openxmlformats-officedocument.wordprocessingml.document"	);
		contenttype.put("docx"	,	"application/vnd.openxmlformats-officedocument.wordprocessingml.document"	);
		contenttype.put("dot"	,	"text/vnd.graphviz"	);
		contenttype.put("dsl"	,	"text/x-dsl"	);
		contenttype.put("dtd"	,	"application/xml-dtd"	);
		contenttype.put("dtx"	,	"text/x-tex"	);
		contenttype.put("dv"	,	"video/dv"	);
		contenttype.put("dvi"	,	"application/x-dvi"	);
		contenttype.put("dvi.bz2"	,	"application/x-bzdvi"	);
		contenttype.put("dvi.gz"	,	"application/x-gzdvi"	);
		contenttype.put("dwg"	,	"image/vnd.dwg"	);
		contenttype.put("dxf"	,	"image/vnd.dxf"	);
		contenttype.put("e"	,	"text/x-eiffel"	);
		contenttype.put("egon"	,	"application/x-egon"	);
		contenttype.put("eif"	,	"text/x-eiffel"	);
		contenttype.put("el"	,	"text/x-emacs-lisp"	);
		contenttype.put("emf"	,	"image/x-emf"	);
		contenttype.put("emp"	,	"application/vnd.emusic-emusic_package"	);
		contenttype.put("ent"	,	"application/xml-external-parsed-entity"	);
		contenttype.put("eps"	,	"image/x-eps"	);
		contenttype.put("eps.bz2"	,	"image/x-bzeps"	);
		contenttype.put("eps.gz"	,	"image/x-gzeps"	);
		contenttype.put("epsf"	,	"image/x-eps"	);
		contenttype.put("epsf.bz2"	,	"image/x-bzeps"	);
		contenttype.put("epsf.gz"	,	"image/x-gzeps"	);
		contenttype.put("epsi"	,	"image/x-eps"	);
		contenttype.put("epsi.bz2"	,	"image/x-bzeps"	);
		contenttype.put("epsi.gz"	,	"image/x-gzeps"	);
		contenttype.put("epub"	,	"application/epub+zip"	);
		contenttype.put("erl"	,	"text/x-erlang"	);
		contenttype.put("es"	,	"application/ecmascript"	);
		contenttype.put("etheme"	,	"application/x-e-theme"	);
		contenttype.put("etx"	,	"text/x-setext"	);
		contenttype.put("exe"	,	"application/x-ms-dos-executable"	);
		contenttype.put("exr"	,	"image/x-exr"	);
		contenttype.put("ez"	,	"application/andrew-inset"	);
		contenttype.put("f"	,	"text/x-fortran"	);
		contenttype.put("f90"	,	"text/x-fortran"	);
		contenttype.put("f95"	,	"text/x-fortran"	);
		contenttype.put("fb2"	,	"application/x-fictionbook+xml"	);
		contenttype.put("fig"	,	"image/x-xfig"	);
		contenttype.put("fits"	,	"image/fits"	);
		contenttype.put("fl"	,	"application/x-fluid"	);
		contenttype.put("flac"	,	"audio/x-flac"	);
		contenttype.put("flc"	,	"video/x-flic"	);
		contenttype.put("fli"	,	"video/x-flic"	);
		contenttype.put("flv"	,	"video/x-flv"	);
		contenttype.put("flw"	,	"application/x-kivio"	);
		contenttype.put("fo"	,	"text/x-xslfo"	);
		contenttype.put("for"	,	"text/x-fortran"	);
		contenttype.put("g3"	,	"image/fax-g3"	);
		contenttype.put("gb"	,	"application/x-gameboy-rom"	);
		contenttype.put("gba"	,	"application/x-gba-rom"	);
		contenttype.put("gcrd"	,	"text/directory"	);
		contenttype.put("ged"	,	"application/x-gedcom"	);
		contenttype.put("gedcom"	,	"application/x-gedcom"	);
		contenttype.put("gen"	,	"application/x-genesis-rom"	);
		contenttype.put("gf"	,	"application/x-tex-gf"	);
		contenttype.put("gg"	,	"application/x-sms-rom"	);
		contenttype.put("gif"	,	"image/gif"	);
		contenttype.put("glade"	,	"application/x-glade"	);
		contenttype.put("gmo"	,	"application/x-gettext-translation"	);
		contenttype.put("gnc"	,	"application/x-gnucash"	);
		contenttype.put("gnd"	,	"application/gnunet-directory"	);
		contenttype.put("gnucash"	,	"application/x-gnucash"	);
		contenttype.put("gnumeric"	,	"application/x-gnumeric"	);
		contenttype.put("gnuplot"	,	"application/x-gnuplot"	);
		contenttype.put("gp"	,	"application/x-gnuplot"	);
		contenttype.put("gpg"	,	"application/pgp-encrypted"	);
		contenttype.put("gplt"	,	"application/x-gnuplot"	);
		contenttype.put("gra"	,	"application/x-graphite"	);
		contenttype.put("gsf"	,	"application/x-font-type1"	);
		contenttype.put("gsm"	,	"audio/x-gsm"	);
		contenttype.put("gtar"	,	"application/x-tar"	);
		contenttype.put("gv"	,	"text/vnd.graphviz"	);
		contenttype.put("gvp"	,	"text/x-google-video-pointer"	);
		contenttype.put("gz"	,	"application/x-gzip"	);
		contenttype.put("h"	,	"text/x-chdr"	);
		contenttype.put("h++"	,	"text/x-c++hdr"	);
		contenttype.put("hdf"	,	"application/x-hdf"	);
		contenttype.put("hh"	,	"text/x-c++hdr"	);
		contenttype.put("hp"	,	"text/x-c++hdr"	);
		contenttype.put("hpgl"	,	"application/vnd.hp-hpgl"	);
		contenttype.put("hpp"	,	"text/x-c++hdr"	);
		contenttype.put("hs"	,	"text/x-haskell"	);
		contenttype.put("htm"	,	"text/html"	);
		contenttype.put("html"	,	"text/html"	);
		contenttype.put("hwp"	,	"application/x-hwp"	);
		contenttype.put("hwt"	,	"application/x-hwt"	);
		contenttype.put("hxx"	,	"text/x-c++hdr"	);
		contenttype.put("ica"	,	"application/x-ica"	);
		contenttype.put("icb"	,	"image/x-tga"	);
		contenttype.put("icns"	,	"image/x-icns"	);
		contenttype.put("ico"	,	"image/vnd.microsoft.icon"	);
		contenttype.put("ics"	,	"text/calendar"	);
		contenttype.put("idl"	,	"text/x-idl"	);
		contenttype.put("ief"	,	"image/ief"	);
		contenttype.put("iff"	,	"image/x-iff"	);
		contenttype.put("ilbm"	,	"image/x-ilbm"	);
		contenttype.put("ime"	,	"text/x-imelody"	);
		contenttype.put("imy"	,	"text/x-imelody"	);
		contenttype.put("ins"	,	"text/x-tex"	);
		contenttype.put("iptables"	,	"text/x-iptables"	);
		contenttype.put("iso"	,	"application/x-cd-image"	);
		contenttype.put("iso9660"	,	"application/x-cd-image"	);
		contenttype.put("it"	,	"audio/x-it"	);
		contenttype.put("j2k"	,	"image/jp2"	);
		contenttype.put("jad"	,	"text/vnd.sun.j2me.app-descriptor"	);
		contenttype.put("jar"	,	"application/x-java-archive"	);
		contenttype.put("java"	,	"text/x-java"	);
		contenttype.put("jng"	,	"image/x-jng"	);
		contenttype.put("jnlp"	,	"application/x-java-jnlp-file"	);
		contenttype.put("jp2"	,	"image/jp2"	);
		contenttype.put("jpc"	,	"image/jp2"	);
		contenttype.put("jpe"	,	"image/jpeg"	);
		contenttype.put("jpeg"	,	"image/jpeg"	);
		contenttype.put("jpf"	,	"image/jp2"	);
		contenttype.put("jpg"	,	"image/jpeg"	);
		contenttype.put("jpr"	,	"application/x-jbuilder-project"	);
		contenttype.put("jpx"	,	"image/jp2"	);
		contenttype.put("js"	,	"application/javascript"	);
		contenttype.put("json"	,	"application/json"	);
		contenttype.put("jsonp"	,	"application/jsonp"	);
		contenttype.put("k25"	,	"image/x-kodak-k25"	);
		contenttype.put("kar"	,	"audio/midi"	);
		contenttype.put("karbon"	,	"application/x-karbon"	);
		contenttype.put("kdc"	,	"image/x-kodak-kdc"	);
		contenttype.put("kdelnk"	,	"application/x-desktop"	);
		contenttype.put("kexi"	,	"application/x-kexiproject-sqlite3"	);
		contenttype.put("kexic"	,	"application/x-kexi-connectiondata"	);
		contenttype.put("kexis"	,	"application/x-kexiproject-shortcut"	);
		contenttype.put("kfo"	,	"application/x-kformula"	);
		contenttype.put("kil"	,	"application/x-killustrator"	);
		contenttype.put("kino"	,	"application/smil"	);
		contenttype.put("kml"	,	"application/vnd.google-earth.kml+xml"	);
		contenttype.put("kmz"	,	"application/vnd.google-earth.kmz"	);
		contenttype.put("kon"	,	"application/x-kontour"	);
		contenttype.put("kpm"	,	"application/x-kpovmodeler"	);
		contenttype.put("kpr"	,	"application/x-kpresenter"	);
		contenttype.put("kpt"	,	"application/x-kpresenter"	);
		contenttype.put("kra"	,	"application/x-krita"	);
		contenttype.put("ksp"	,	"application/x-kspread"	);
		contenttype.put("kud"	,	"application/x-kugar"	);
		contenttype.put("kwd"	,	"application/x-kword"	);
		contenttype.put("kwt"	,	"application/x-kword"	);
		contenttype.put("la"	,	"application/x-shared-library-la"	);
		contenttype.put("latex"	,	"text/x-tex"	);
		contenttype.put("ldif"	,	"text/x-ldif"	);
		contenttype.put("lha"	,	"application/x-lha"	);
		contenttype.put("lhs"	,	"text/x-literate-haskell"	);
		contenttype.put("lhz"	,	"application/x-lhz"	);
		contenttype.put("log"	,	"text/x-log"	);
		contenttype.put("ltx"	,	"text/x-tex"	);
		contenttype.put("lua"	,	"text/x-lua"	);
		contenttype.put("lwo"	,	"image/x-lwo"	);
		contenttype.put("lwob"	,	"image/x-lwo"	);
		contenttype.put("lws"	,	"image/x-lws"	);
		contenttype.put("ly"	,	"text/x-lilypond"	);
		contenttype.put("lyx"	,	"application/x-lyx"	);
		contenttype.put("lz"	,	"application/x-lzip"	);
		contenttype.put("lzh"	,	"application/x-lha"	);
		contenttype.put("lzma"	,	"application/x-lzma"	);
		contenttype.put("lzo"	,	"application/x-lzop"	);
		contenttype.put("m"	,	"text/x-matlab"	);
		contenttype.put("m15"	,	"audio/x-mod"	);
		contenttype.put("m2t"	,	"video/mpeg"	);
		contenttype.put("m3u"	,	"audio/x-mpegurl"	);
		contenttype.put("m3u8"	,	"audio/x-mpegurl"	);
		contenttype.put("m4"	,	"application/x-m4"	);
		contenttype.put("m4a"	,	"audio/mp4"	);
		contenttype.put("m4b"	,	"audio/x-m4b"	);
		contenttype.put("m4v"	,	"video/mp4"	);
		contenttype.put("mab"	,	"application/x-markaby"	);
		contenttype.put("man"	,	"application/x-troff-man"	);
		contenttype.put("mbox"	,	"application/mbox"	);
		contenttype.put("md"	,	"application/x-genesis-rom"	);
		contenttype.put("mdb"	,	"application/vnd.ms-access"	);
		contenttype.put("mdi"	,	"image/vnd.ms-modi"	);
		contenttype.put("me"	,	"text/x-troff-me"	);
		contenttype.put("med"	,	"audio/x-mod"	);
		contenttype.put("metalink"	,	"application/metalink+xml"	);
		contenttype.put("mgp"	,	"application/x-magicpoint"	);
		contenttype.put("mid"	,	"audio/midi"	);
		contenttype.put("midi"	,	"audio/midi"	);
		contenttype.put("mif"	,	"application/x-mif"	);
		contenttype.put("minipsf"	,	"audio/x-minipsf"	);
		contenttype.put("mka"	,	"audio/x-matroska"	);
		contenttype.put("mkv"	,	"video/x-matroska"	);
		contenttype.put("ml"	,	"text/x-ocaml"	);
		contenttype.put("mli"	,	"text/x-ocaml"	);
		contenttype.put("mm"	,	"text/x-troff-mm"	);
		contenttype.put("mmf"	,	"application/x-smaf"	);
		contenttype.put("mml"	,	"text/mathml"	);
		contenttype.put("mng"	,	"video/x-mng"	);
		contenttype.put("mo"	,	"application/x-gettext-translation"	);
		contenttype.put("mo3"	,	"audio/x-mo3"	);
		contenttype.put("moc"	,	"text/x-moc"	);
		contenttype.put("mod"	,	"audio/x-mod"	);
		contenttype.put("mof"	,	"text/x-mof"	);
		contenttype.put("moov"	,	"video/quicktime"	);
		contenttype.put("mov"	,	"video/quicktime"	);
		contenttype.put("movie"	,	"video/x-sgi-movie"	);
		contenttype.put("mp+"	,	"audio/x-musepack"	);
		contenttype.put("mp2"	,	"video/mpeg"	);
		contenttype.put("mp3"	,	"audio/mpeg"	);
		contenttype.put("mp4"	,	"video/mp4"	);
		contenttype.put("mpc"	,	"audio/x-musepack"	);
		contenttype.put("mpe"	,	"video/mpeg"	);
		contenttype.put("mpeg"	,	"video/mpeg"	);
		contenttype.put("mpg"	,	"video/mpeg"	);
		contenttype.put("mpga"	,	"audio/mpeg"	);
		contenttype.put("mpp"	,	"audio/x-musepack"	);
		contenttype.put("mrl"	,	"text/x-mrml"	);
		contenttype.put("mrml"	,	"text/x-mrml"	);
		contenttype.put("mrw"	,	"image/x-minolta-mrw"	);
		contenttype.put("ms"	,	"text/x-troff-ms"	);
		contenttype.put("msi"	,	"application/x-msi"	);
		contenttype.put("msod"	,	"image/x-msod"	);
		contenttype.put("msx"	,	"application/x-msx-rom"	);
		contenttype.put("mtm"	,	"audio/x-mod"	);
		contenttype.put("mup"	,	"text/x-mup"	);
		contenttype.put("mxf"	,	"application/mxf"	);
		contenttype.put("n64"	,	"application/x-n64-rom"	);
		contenttype.put("nb"	,	"application/mathematica"	);
		contenttype.put("nc"	,	"application/x-netcdf"	);
		contenttype.put("nds"	,	"application/x-nintendo-ds-rom"	);
		contenttype.put("nef"	,	"image/x-nikon-nef"	);
		contenttype.put("nes"	,	"application/x-nes-rom"	);
		contenttype.put("nfo"	,	"text/x-nfo"	);
		contenttype.put("not"	,	"text/x-mup"	);
		contenttype.put("nsc"	,	"application/x-netshow-channel"	);
		contenttype.put("nsv"	,	"video/x-nsv"	);
		contenttype.put("o"	,	"application/x-object"	);
		contenttype.put("obj"	,	"application/x-tgif"	);
		contenttype.put("ocl"	,	"text/x-ocl"	);
		contenttype.put("oda"	,	"application/oda"	);
		contenttype.put("odb"	,	"application/vnd.oasis.opendocument.database"	);
		contenttype.put("odc"	,	"application/vnd.oasis.opendocument.chart"	);
		contenttype.put("odf"	,	"application/vnd.oasis.opendocument.formula"	);
		contenttype.put("odg"	,	"application/vnd.oasis.opendocument.graphics"	);
		contenttype.put("odi"	,	"application/vnd.oasis.opendocument.image"	);
		contenttype.put("odm"	,	"application/vnd.oasis.opendocument.text-master"	);
		contenttype.put("odp"	,	"application/vnd.oasis.opendocument.presentation"	);
		contenttype.put("ods"	,	"application/vnd.oasis.opendocument.spreadsheet"	);
		contenttype.put("odt"	,	"application/vnd.oasis.opendocument.text"	);
		contenttype.put("oga"	,	"audio/ogg"	);
		contenttype.put("ogg"	,	"video/x-theora+ogg"	);
		contenttype.put("ogm"	,	"video/x-ogm+ogg"	);
		contenttype.put("ogv"	,	"video/ogg"	);
		contenttype.put("ogx"	,	"application/ogg"	);
		contenttype.put("old"	,	"application/x-trash"	);
		contenttype.put("oleo"	,	"application/x-oleo"	);
		contenttype.put("opml"	,	"text/x-opml+xml"	);
		contenttype.put("ora"	,	"image/openraster"	);
		contenttype.put("orf"	,	"image/x-olympus-orf"	);
		contenttype.put("otc"	,	"application/vnd.oasis.opendocument.chart-template"	);
		contenttype.put("otf"	,	"application/x-font-otf"	);
		contenttype.put("otg"	,	"application/vnd.oasis.opendocument.graphics-template"	);
		contenttype.put("oth"	,	"application/vnd.oasis.opendocument.text-web"	);
		contenttype.put("otp"	,	"application/vnd.oasis.opendocument.presentation-template"	);
		contenttype.put("ots"	,	"application/vnd.oasis.opendocument.spreadsheet-template"	);
		contenttype.put("ott"	,	"application/vnd.oasis.opendocument.text-template"	);
		contenttype.put("owl"	,	"application/rdf+xml"	);
		contenttype.put("oxt"	,	"application/vnd.openofficeorg.extension"	);
		contenttype.put("p"	,	"text/x-pascal"	);
		contenttype.put("p10"	,	"application/pkcs10"	);
		contenttype.put("p12"	,	"application/x-pkcs12"	);
		contenttype.put("p7b"	,	"application/x-pkcs7-certificates"	);
		contenttype.put("p7s"	,	"application/pkcs7-signature"	);
		contenttype.put("pack"	,	"application/x-java-pack200"	);
		contenttype.put("pak"	,	"application/x-pak"	);
		contenttype.put("par2"	,	"application/x-par2"	);
		contenttype.put("pas"	,	"text/x-pascal"	);
		contenttype.put("patch"	,	"text/x-patch"	);
		contenttype.put("pbm"	,	"image/x-portable-bitmap"	);
		contenttype.put("pcd"	,	"image/x-photo-cd"	);
		contenttype.put("pcf"	,	"application/x-cisco-vpn-settings"	);
		contenttype.put("pcf.gz"	,	"application/x-font-pcf"	);
		contenttype.put("pcf.z"	,	"application/x-font-pcf"	);
		contenttype.put("pcl"	,	"application/vnd.hp-pcl"	);
		contenttype.put("pcx"	,	"image/x-pcx"	);
		contenttype.put("pdb"	,	"chemical/x-pdb"	);
		contenttype.put("pdc"	,	"application/x-aportisdoc"	);
		contenttype.put("pdf"	,	"application/pdf"	);
		contenttype.put("pdf.bz2"	,	"application/x-bzpdf"	);
		contenttype.put("pdf.gz"	,	"application/x-gzpdf"	);
		contenttype.put("pef"	,	"image/x-pentax-pef"	);
		contenttype.put("pem"	,	"application/x-x509-ca-cert"	);
		contenttype.put("perl"	,	"application/x-perl"	);
		contenttype.put("pfa"	,	"application/x-font-type1"	);
		contenttype.put("pfb"	,	"application/x-font-type1"	);
		contenttype.put("pfx"	,	"application/x-pkcs12"	);
		contenttype.put("pgm"	,	"image/x-portable-graymap"	);
		contenttype.put("pgn"	,	"application/x-chess-pgn"	);
		contenttype.put("pgp"	,	"application/pgp-encrypted"	);
		contenttype.put("php"	,	"application/x-php"	);
		contenttype.put("php3"	,	"application/x-php"	);
		contenttype.put("php4"	,	"application/x-php"	);
		contenttype.put("pict"	,	"image/x-pict"	);
		contenttype.put("pict1"	,	"image/x-pict"	);
		contenttype.put("pict2"	,	"image/x-pict"	);
		contenttype.put("pickle"	,	"application/python-pickle"	);
		contenttype.put("pk"	,	"application/x-tex-pk"	);
		contenttype.put("pkipath"	,	"application/pkix-pkipath"	);
		contenttype.put("pkr"	,	"application/pgp-keys"	);
		contenttype.put("pl"	,	"application/x-perl"	);
		contenttype.put("pla"	,	"audio/x-iriver-pla"	);
		contenttype.put("pln"	,	"application/x-planperfect"	);
		contenttype.put("pls"	,	"audio/x-scpls"	);
		contenttype.put("pm"	,	"application/x-perl"	);
		contenttype.put("png"	,	"image/png"	);
		contenttype.put("pnm"	,	"image/x-portable-anymap"	);
		contenttype.put("pntg"	,	"image/x-macpaint"	);
		contenttype.put("po"	,	"text/x-gettext-translation"	);
		contenttype.put("por"	,	"application/x-spss-por"	);
		contenttype.put("pot"	,	"text/x-gettext-translation-template"	);
		contenttype.put("ppm"	,	"image/x-portable-pixmap"	);
		contenttype.put("pps"	,	"application/vnd.ms-powerpoint"	);
		contenttype.put("ppt"	,	"application/vnd.ms-powerpoint"	);
		contenttype.put("pptm"	,	"application/vnd.openxmlformats-officedocument.presentationml.presentation"	);
		contenttype.put("pptx"	,	"application/vnd.openxmlformats-officedocument.presentationml.presentation"	);
		contenttype.put("ppz"	,	"application/vnd.ms-powerpoint"	);
		contenttype.put("prc"	,	"application/x-palm-database"	);
		contenttype.put("ps"	,	"application/postscript"	);
		contenttype.put("ps.bz2"	,	"application/x-bzpostscript"	);
		contenttype.put("ps.gz"	,	"application/x-gzpostscript"	);
		contenttype.put("psd"	,	"image/vnd.adobe.photoshop"	);
		contenttype.put("psf"	,	"audio/x-psf"	);
		contenttype.put("psf.gz"	,	"application/x-gz-font-linux-psf"	);
		contenttype.put("psflib"	,	"audio/x-psflib"	);
		contenttype.put("psid"	,	"audio/prs.sid"	);
		contenttype.put("psw"	,	"application/x-pocket-word"	);
		contenttype.put("pw"	,	"application/x-pw"	);
		contenttype.put("py"	,	"text/x-python"	);
		contenttype.put("pyc"	,	"application/x-python-bytecode"	);
		contenttype.put("pyo"	,	"application/x-python-bytecode"	);
		contenttype.put("qif"	,	"image/x-quicktime"	);
		contenttype.put("qt"	,	"video/quicktime"	);
		contenttype.put("qtif"	,	"image/x-quicktime"	);
		contenttype.put("qtl"	,	"application/x-quicktime-media-link"	);
		contenttype.put("qtvr"	,	"video/quicktime"	);
		contenttype.put("ra"	,	"audio/vnd.rn-realaudio"	);
		contenttype.put("raf"	,	"image/x-fuji-raf"	);
		contenttype.put("ram"	,	"application/ram"	);
		contenttype.put("rar"	,	"application/x-rar"	);
		contenttype.put("ras"	,	"image/x-cmu-raster"	);
		contenttype.put("raw"	,	"image/x-panasonic-raw"	);
		contenttype.put("rax"	,	"audio/vnd.rn-realaudio"	);
		contenttype.put("rb"	,	"application/x-ruby"	);
		contenttype.put("rdf"	,	"application/rdf+xml"	);
		contenttype.put("rdfs"	,	"application/rdf+xml"	);
		contenttype.put("reg"	,	"text/x-ms-regedit"	);
		contenttype.put("rej"	,	"application/x-reject"	);
		contenttype.put("rgb"	,	"image/x-rgb"	);
		contenttype.put("rle"	,	"image/rle"	);
		contenttype.put("rm"	,	"application/vnd.rn-realmedia"	);
		contenttype.put("rmj"	,	"application/vnd.rn-realmedia"	);
		contenttype.put("rmm"	,	"application/vnd.rn-realmedia"	);
		contenttype.put("rms"	,	"application/vnd.rn-realmedia"	);
		contenttype.put("rmvb"	,	"application/vnd.rn-realmedia"	);
		contenttype.put("rmx"	,	"application/vnd.rn-realmedia"	);
		contenttype.put("roff"	,	"text/troff"	);
		contenttype.put("rp"	,	"image/vnd.rn-realpix"	);
		contenttype.put("rpm"	,	"application/x-rpm"	);
		contenttype.put("rss"	,	"application/rss+xml"	);
		contenttype.put("rt"	,	"text/vnd.rn-realtext"	);
		contenttype.put("rtf"	,	"application/rtf"	);
		contenttype.put("rtx"	,	"text/richtext"	);
		contenttype.put("rv"	,	"video/vnd.rn-realvideo"	);
		contenttype.put("rvx"	,	"video/vnd.rn-realvideo"	);
		contenttype.put("s3m"	,	"audio/x-s3m"	);
		contenttype.put("sam"	,	"application/x-amipro"	);
		contenttype.put("sami"	,	"application/x-sami"	);
		contenttype.put("sav"	,	"application/x-spss-sav"	);
		contenttype.put("scm"	,	"text/x-scheme"	);
		contenttype.put("sda"	,	"application/vnd.stardivision.draw"	);
		contenttype.put("sdc"	,	"application/vnd.stardivision.calc"	);
		contenttype.put("sdd"	,	"application/vnd.stardivision.impress"	);
		contenttype.put("sdp"	,	"application/sdp"	);
		contenttype.put("sds"	,	"application/vnd.stardivision.chart"	);
		contenttype.put("sdw"	,	"application/vnd.stardivision.writer"	);
		contenttype.put("sgf"	,	"application/x-go-sgf"	);
		contenttype.put("sgi"	,	"image/x-sgi"	);
		contenttype.put("sgl"	,	"application/vnd.stardivision.writer"	);
		contenttype.put("sgm"	,	"text/sgml"	);
		contenttype.put("sgml"	,	"text/sgml"	);
		contenttype.put("sh"	,	"application/x-shellscript"	);
		contenttype.put("shar"	,	"application/x-shar"	);
		contenttype.put("shn"	,	"application/x-shorten"	);
		contenttype.put("siag"	,	"application/x-siag"	);
		contenttype.put("sid"	,	"audio/prs.sid"	);
		contenttype.put("sik"	,	"application/x-trash"	);
		contenttype.put("sis"	,	"application/vnd.symbian.install"	);
		contenttype.put("sisx"	,	"x-epoc/x-sisx-app"	);
		contenttype.put("sit"	,	"application/x-stuffit"	);
		contenttype.put("siv"	,	"application/sieve"	);
		contenttype.put("sk"	,	"image/x-skencil"	);
		contenttype.put("sk1"	,	"image/x-skencil"	);
		contenttype.put("skr"	,	"application/pgp-keys"	);
		contenttype.put("slk"	,	"text/spreadsheet"	);
		contenttype.put("smaf"	,	"application/x-smaf"	);
		contenttype.put("smc"	,	"application/x-snes-rom"	);
		contenttype.put("smd"	,	"application/vnd.stardivision.mail"	);
		contenttype.put("smf"	,	"application/vnd.stardivision.math"	);
		contenttype.put("smi"	,	"application/x-sami"	);
		contenttype.put("smil"	,	"application/smil"	);
		contenttype.put("sml"	,	"application/smil"	);
		contenttype.put("sms"	,	"application/x-sms-rom"	);
		contenttype.put("snd"	,	"audio/basic"	);
		contenttype.put("so"	,	"application/x-sharedlib"	);
		contenttype.put("spc"	,	"application/x-pkcs7-certificates"	);
		contenttype.put("spd"	,	"application/x-font-speedo"	);
		contenttype.put("spec"	,	"text/x-rpm-spec"	);
		contenttype.put("spl"	,	"application/x-shockwave-flash"	);
		contenttype.put("spx"	,	"audio/x-speex"	);
		contenttype.put("sql"	,	"text/x-sql"	);
		contenttype.put("sr2"	,	"image/x-sony-sr2"	);
		contenttype.put("src"	,	"application/x-wais-source"	);
		contenttype.put("srf"	,	"image/x-sony-srf"	);
		contenttype.put("srt"	,	"application/x-subrip"	);
		contenttype.put("ssa"	,	"text/x-ssa"	);
		contenttype.put("stc"	,	"application/vnd.sun.xml.calc.template"	);
		contenttype.put("std"	,	"application/vnd.sun.xml.draw.template"	);
		contenttype.put("sti"	,	"application/vnd.sun.xml.impress.template"	);
		contenttype.put("stm"	,	"audio/x-stm"	);
		contenttype.put("stw"	,	"application/vnd.sun.xml.writer.template"	);
		contenttype.put("sty"	,	"text/x-tex"	);
		contenttype.put("sub"	,	"text/x-subviewer"	);
		contenttype.put("sun"	,	"image/x-sun-raster"	);
		contenttype.put("sv4cpio"	,	"application/x-sv4cpio"	);
		contenttype.put("sv4crc"	,	"application/x-sv4crc"	);
		contenttype.put("svg"	,	"image/svg+xml"	);
		contenttype.put("svgz"	,	"image/svg+xml-compressed"	);
		contenttype.put("swf"	,	"application/x-shockwave-flash"	);
		contenttype.put("sxc"	,	"application/vnd.sun.xml.calc"	);
		contenttype.put("sxd"	,	"application/vnd.sun.xml.draw"	);
		contenttype.put("sxg"	,	"application/vnd.sun.xml.writer.global"	);
		contenttype.put("sxi"	,	"application/vnd.sun.xml.impress"	);
		contenttype.put("sxm"	,	"application/vnd.sun.xml.math"	);
		contenttype.put("sxw"	,	"application/vnd.sun.xml.writer"	);
		contenttype.put("sylk"	,	"text/spreadsheet"	);
		contenttype.put("t"	,	"text/troff"	);
		contenttype.put("t2t"	,	"text/x-txt2tags"	);
		contenttype.put("tar"	,	"application/x-tar"	);
		contenttype.put("tar.bz"	,	"application/x-bzip-compressed-tar"	);
		contenttype.put("tar.bz2"	,	"application/x-bzip-compressed-tar"	);
		contenttype.put("tar.gz"	,	"application/x-compressed-tar"	);
		contenttype.put("tar.lzma"	,	"application/x-lzma-compressed-tar"	);
		contenttype.put("tar.lzo"	,	"application/x-tzo"	);
		contenttype.put("tar.xz"	,	"application/x-xz-compressed-tar"	);
		contenttype.put("tar.z"	,	"application/x-tarz"	);
		contenttype.put("tbz"	,	"application/x-bzip-compressed-tar"	);
		contenttype.put("tbz2"	,	"application/x-bzip-compressed-tar"	);
		contenttype.put("tcl"	,	"text/x-tcl"	);
		contenttype.put("tex"	,	"text/x-tex"	);
		contenttype.put("texi"	,	"text/x-texinfo"	);
		contenttype.put("texinfo"	,	"text/x-texinfo"	);
		contenttype.put("tga"	,	"image/x-tga"	);
		contenttype.put("tgz"	,	"application/x-compressed-tar"	);
		contenttype.put("theme"	,	"application/x-theme"	);
		contenttype.put("themepack"	,	"application/x-windows-themepack"	);
		contenttype.put("tif"	,	"image/tiff"	);
		contenttype.put("tiff"	,	"image/tiff"	);
		contenttype.put("tk"	,	"text/x-tcl"	);
		contenttype.put("tlz"	,	"application/x-lzma-compressed-tar"	);
		contenttype.put("tnef"	,	"application/vnd.ms-tnef"	);
		contenttype.put("tnf"	,	"application/vnd.ms-tnef"	);
		contenttype.put("toc"	,	"application/x-cdrdao-toc"	);
		contenttype.put("torrent"	,	"application/x-bittorrent"	);
		contenttype.put("tpic"	,	"image/x-tga"	);
		contenttype.put("tr"	,	"text/troff"	);
		contenttype.put("ts"	,	"application/x-linguist"	);
		contenttype.put("tsv"	,	"text/tab-separated-values"	);
		contenttype.put("tta"	,	"audio/x-tta"	);
		contenttype.put("ttc"	,	"application/x-font-ttf"	);
		contenttype.put("ttf"	,	"application/x-font-ttf"	);
		contenttype.put("ttx"	,	"application/x-font-ttx"	);
		contenttype.put("txt"	,	"text/plain"	);
		contenttype.put("txz"	,	"application/x-xz-compressed-tar"	);
		contenttype.put("tzo"	,	"application/x-tzo"	);
		contenttype.put("ufraw"	,	"application/x-ufraw"	);
		contenttype.put("ui"	,	"application/x-designer"	);
		contenttype.put("uil"	,	"text/x-uil"	);
		contenttype.put("ult"	,	"audio/x-mod"	);
		contenttype.put("uni"	,	"audio/x-mod"	);
		contenttype.put("uri"	,	"text/x-uri"	);
		contenttype.put("url"	,	"text/x-uri"	);
		contenttype.put("ustar"	,	"application/x-ustar"	);
		contenttype.put("vala"	,	"text/x-vala"	);
		contenttype.put("vapi"	,	"text/x-vala"	);
		contenttype.put("vcf"	,	"text/directory"	);
		contenttype.put("vcs"	,	"text/calendar"	);
		contenttype.put("vct"	,	"text/directory"	);
		contenttype.put("vda"	,	"image/x-tga"	);
		contenttype.put("vhd"	,	"text/x-vhdl"	);
		contenttype.put("vhdl"	,	"text/x-vhdl"	);
		contenttype.put("viv"	,	"video/vivo"	);
		contenttype.put("vivo"	,	"video/vivo"	);
		contenttype.put("vlc"	,	"audio/x-mpegurl"	);
		contenttype.put("vob"	,	"video/mpeg"	);
		contenttype.put("voc"	,	"audio/x-voc"	);
		contenttype.put("vor"	,	"application/vnd.stardivision.writer"	);
		contenttype.put("vst"	,	"image/x-tga"	);
		contenttype.put("wav"	,	"audio/x-wav"	);
		contenttype.put("wax"	,	"audio/x-ms-asx"	);
		contenttype.put("wb1"	,	"application/x-quattropro"	);
		contenttype.put("wb2"	,	"application/x-quattropro"	);
		contenttype.put("wb3"	,	"application/x-quattropro"	);
		contenttype.put("wbmp"	,	"image/vnd.wap.wbmp"	);
		contenttype.put("wcm"	,	"application/vnd.ms-works"	);
		contenttype.put("wdb"	,	"application/vnd.ms-works"	);
		contenttype.put("webm"	,	"video/webm"	);
		contenttype.put("wk1"	,	"application/vnd.lotus-1-2-3"	);
		contenttype.put("wk3"	,	"application/vnd.lotus-1-2-3"	);
		contenttype.put("wk4"	,	"application/vnd.lotus-1-2-3"	);
		contenttype.put("wks"	,	"application/vnd.ms-works"	);
		contenttype.put("wma"	,	"audio/x-ms-wma"	);
		contenttype.put("wmf"	,	"image/x-wmf"	);
		contenttype.put("wml"	,	"text/vnd.wap.wml"	);
		contenttype.put("wmls"	,	"text/vnd.wap.wmlscript"	);
		contenttype.put("wmv"	,	"video/x-ms-wmv"	);
		contenttype.put("wmx"	,	"audio/x-ms-asx"	);
		contenttype.put("wp"	,	"application/vnd.wordperfect"	);
		contenttype.put("wp4"	,	"application/vnd.wordperfect"	);
		contenttype.put("wp5"	,	"application/vnd.wordperfect"	);
		contenttype.put("wp6"	,	"application/vnd.wordperfect"	);
		contenttype.put("wpd"	,	"application/vnd.wordperfect"	);
		contenttype.put("wpg"	,	"application/x-wpg"	);
		contenttype.put("wpl"	,	"application/vnd.ms-wpl"	);
		contenttype.put("wpp"	,	"application/vnd.wordperfect"	);
		contenttype.put("wps"	,	"application/vnd.ms-works"	);
		contenttype.put("wri"	,	"application/x-mswrite"	);
		contenttype.put("wrl"	,	"model/vrml"	);
		contenttype.put("wv"	,	"audio/x-wavpack"	);
		contenttype.put("wvc"	,	"audio/x-wavpack-correction"	);
		contenttype.put("wvp"	,	"audio/x-wavpack"	);
		contenttype.put("wvx"	,	"audio/x-ms-asx"	);
		contenttype.put("x3f"	,	"image/x-sigma-x3f"	);
		contenttype.put("xac"	,	"application/x-gnucash"	);
		contenttype.put("xbel"	,	"application/x-xbel"	);
		contenttype.put("xbl"	,	"application/xml"	);
		contenttype.put("xbm"	,	"image/x-xbitmap"	);
		contenttype.put("xcf"	,	"image/x-xcf"	);
		contenttype.put("xcf.bz2"	,	"image/x-compressed-xcf"	);
		contenttype.put("xcf.gz"	,	"image/x-compressed-xcf"	);
		contenttype.put("xhtml"	,	"application/xhtml+xml"	);
		contenttype.put("xi"	,	"audio/x-xi"	);
		contenttype.put("xla"	,	"application/vnd.ms-excel"	);
		contenttype.put("xlc"	,	"application/vnd.ms-excel"	);
		contenttype.put("xld"	,	"application/vnd.ms-excel"	);
		contenttype.put("xlf"	,	"application/x-xliff"	);
		contenttype.put("xliff"	,	"application/x-xliff"	);
		contenttype.put("xll"	,	"application/vnd.ms-excel"	);
		contenttype.put("xlm"	,	"application/vnd.ms-excel"	);
		contenttype.put("xls"	,	"application/vnd.ms-excel"	);
		contenttype.put("xlsm"	,	"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"	);
		contenttype.put("xlsx"	,	"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"	);
		contenttype.put("xlt"	,	"application/vnd.ms-excel"	);
		contenttype.put("xlw"	,	"application/vnd.ms-excel"	);
		contenttype.put("xm"	,	"audio/x-xm"	);
		contenttype.put("xmf"	,	"audio/x-xmf"	);
		contenttype.put("xmi"	,	"text/x-xmi"	);
		contenttype.put("xml"	,	"application/xml"	);
		contenttype.put("xpm"	,	"image/x-xpixmap"	);
		contenttype.put("xps"	,	"application/vnd.ms-xpsdocument"	);
		contenttype.put("xsl"	,	"application/xml"	);
		contenttype.put("xslfo"	,	"text/x-xslfo"	);
		contenttype.put("xslt"	,	"application/xml"	);
		contenttype.put("xspf"	,	"application/xspf+xml"	);
		contenttype.put("xul"	,	"application/vnd.mozilla.xul+xml"	);
		contenttype.put("xwd"	,	"image/x-xwindowdump"	);
		contenttype.put("xyz"	,	"chemical/x-pdb"	);
		contenttype.put("xz"	,	"application/x-xz"	);
		contenttype.put("w2p"	,	"application/w2p"	);
		contenttype.put("z"	,	"application/x-compress"	);
		contenttype.put("zabw"	,	"application/x-abiword"	);
		contenttype.put("zip"	,	"application/zip"	);
		contenttype.put("zoo"	,	"application/x-zoo"	);

	}
	
	
	
}
