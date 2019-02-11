import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

class FFMPegTask implements Callable<Integer> {
	def srcFile;
	def destFile;
	def ffOptsAry = [];
	public FFMPegTask(fromFile, toFile){
		srcFile = fromFile;
		destFile = toFile;
		// FILTERS
		ffOptsAry << " -vf yadif"   // de-interlacing
		
		// VIDEO ENCODING OPTIONS
		//ffOptsAry << " -vcodec libx264";
		ffOptsAry << " -vcodec libx264";
		ffOptsAry << " -preset medium";  // balance encoding speed vs compression ratio
		ffOptsAry << " -profile:v main -level 3.0 ";  // compatibility, see https://trac.ffmpeg.org/wiki/Encode/H.264
		ffOptsAry << " -pix_fmt yuv420p ";  // pixel format of MiniDV is yuv411, x264 supports yuv420
		ffOptsAry << " -crf 23";  // The constant quality setting. Higher value = less quality, smaller file. Lower = better quality, bigger file. Sane values are [18 - 24]
		ffOptsAry << " -x264-params ref=4";
		
		// AUDIO ENCODING OPTIONS
		ffOptsAry << " -acodec aac";
		ffOptsAry << " -ac 2 -ar 24000 -ab 80k";  // 2 channels, 24k sample rate, 80k bitrate
		
		// GENERIC OPTIONS
		ffOptsAry << " -movflags faststart";  // Run a second pass moving the index (moov atom) to the beginning of the file.
	}

	@Override
	public Integer call() throws Exception {
		def threadId = Thread.currentThread().getId();
		println "Starting thread: $threadId  $srcFile -> $destFile";		
		def ffopts = ffOptsAry.join(" ");
//		ffopts = " -vcodec h264 -g 30 -deinterlace -b 900k -s 720x480 -padtop 60 -padbottom 60 -padcolor 000000 -aspect 4:3 -acodec mp3 -ab 64k ";
		def sout = new StringBuffer();
		def serr = new StringBBuffer();
		def str = "ffmpeg -f dv -i $srcFile $ffopts -y $destFile";
		def proc = str.execute(); 
		def outputStream = new StringBuffer();
		def errorStream = new StringBuffer();

//		proc.consumeProcessOutput(sout,serr);
//		println "Sout: "+sout;
//		println "Serr: "+serr;
		println "Finished thread: $threadId  $srcFile -> $destFile";		
		return new Integer(0);
	}
}

def basePath = "/data/odin/video";
def srcPath= "$basePath/save";
def destPath = "$basePath/mp4";

def executor = Executors.newFixedThreadPool(1);
def list = new ArrayList<Future<Integer>>();

def files = new File(srcPath).listFiles().sort();
def nFiles = 0;
files.each{ file->
	def fileName = file.getName();
	if ( nFiles < 5 ){

		// define the paths
		def srcFileFullPath = "$srcPath/$fileName";
		def destFile = fileName.replace(".dv",".mp4");
		def destFileFullPath = "$destPath/$destFile";
		def worker = new FFMPegTask(srcFileFullPath,destFileFullPath);

		println "!!! \t\t $srcFileFullPath  ==>  $destFileFullPath";
		list.add(executor.submit(worker));
	} else {
//		println "!!! Skip $fileName for now";
	}
	nFiles++;
}

println "Waiting for futures , sleep first...";
TimeUnit.SECONDS.sleep(5);
println "Waking up";
for(Future<Integer> future:list){
	try {
		println "Getting output: "+future.get();
	} catch(InterruptedException e) {
		e.printStackTrace();
	} catch(ExecutionException e) {
		e.printStackTrace();
	}
}
println "Done";
executor.shutdown();

