package es.pagoru.CompilerLessMin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.lesscss.LessCompiler;

/**
 * 
 * @author Pablo
 * @since 28/05/2016
 *
 */
public class CompilerLessMin {

	public static void main(String[] args) throws Exception {
		
		boolean loop = true;
		loadConfig();
		
		LessCompiler lessCompiler = new LessCompiler();
		
		while(loop){
			String current = read();
			
			switch (current) {
			case "compile-min":				
				FileWriter writer = new FileWriter(
						new File(config.getProperty("css")));
		        writer.append(getMinified(lessCompiler.compile(new File(config.getProperty("less")))));
		        writer.close();
				
				System.out.println("Compiled less, minified and saved.");
				break;
				
			case "compile":
				lessCompiler.compile(new File(config.getProperty("less")), new File(config.getProperty("css")));
				
				System.out.println("Compiled less and saved.");
				break;
			
			case "stop":
				loop = false;
				System.out.println("STOP!");
				break;
			
			case "load":
				loadConfig();
				break;
				
			default:
				break;
			}
		}
		
	}
	
	private static String getMinified(String css) throws Exception {

		String url = "http://cssminifier.com/raw";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "input=" + css;
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		return response.toString();

	}
	
	private static final String DEFAULT_CONFIG =
            "#localización de css\n" +
            "less = C:\\css.less\n" +
            "#localizacion donde guardar el archivo min de css \n" +
            "css = C:\\css-min.css\n";
    private static Properties config;
    
	private static void createDefaultConfigFile(File f) throws IOException {
        FileWriter writer = new FileWriter(f);
        writer.append(DEFAULT_CONFIG);
        writer.close();
    }
	private static void loadConfig() throws IOException {
        File f = new File("./config.txt");
        if (!f.exists())createDefaultConfigFile(f);
        Scanner s = new Scanner(new FileInputStream(f));

        String css = null;
        String less = null;
        while (s.hasNext()){
            String line = s.nextLine();
            if (line.startsWith("#"))continue;
            if (line.startsWith("css =")){
                css = line.replaceFirst("css =", "").trim();
            }else if(line.startsWith("less =")){
                less = line.replaceFirst("less =", "").trim();
            }else{
                System.err.println("Ignorando linea de la configuracion: "+line);
            }
        }

        config = new Properties();
        config.setProperty("css", css);
        config.setProperty("less", less);
        System.out.println("Configuracion: "+config);
        
        s.close();
    }
	
	private static Scanner sc = new Scanner(System.in);
	private static String read(){
		return sc.nextLine();
	}
	
}
