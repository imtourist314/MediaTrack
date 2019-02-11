
println "Start\n";
str = "ffmpeg -f dv -i /data/odin/video/save/aa-2004.04.21_22-19-35.dv  -vf yadif  -vcodec libx264  -preset medium  -profile:v main -level 3.0   -pix_fmt yuv420p   -crf 23  -x264-params ref=4  -acodec aac  -ac 2 -ar 24000 -ab 80k  -movflags faststart -y /data/odin/video/mp4/aa-2004.04.21_22-19-35.mp4";

def proc = str.execute();
def outputStream = new StringBuffer();
def errorStream = new StringBuffer();
proc.waitForProcessOutput(outputStream, errorStream);

println "Finish\n";

System.sleep(5);

def n;
def a = outputStream.toString().split("\\n");
println "Output string";
n = 0;
a.each{
	println "Output: "+n+" "+it;
	n++;
}

def b = errorStream.toString().split("\\n");
println "Error string";
n = 0;
b.each{
	println "Error: "+n+" "+it;
	n++;
}

println "Done";
