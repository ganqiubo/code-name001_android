package tl.pojul.com.fastim.http.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduTranslateConverter {

    public String converterZhToEn(String response){
        Pattern pattern = Pattern.compile("<p class=\"ordinary-output target-output.*?</span> </p>");
        Matcher matcher = pattern.matcher(response);
        String translateLabel = "";
        while(matcher.find()){
            translateLabel = response.substring(matcher.start(), matcher.end());
        }
        pattern = Pattern.compile(">.*?</span> </p>");
        matcher = pattern.matcher(translateLabel);
        String translate = "";
        while (matcher.find()){
            translate = translateLabel.substring((matcher.start() + 1), (matcher.end() - 12));
        }
        translate.replace(".", "");
        return translate;
    }

}
