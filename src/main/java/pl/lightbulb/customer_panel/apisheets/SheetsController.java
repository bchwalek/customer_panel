package pl.lightbulb.customer_panel.apisheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.lightbulb.customer_panel.Photo;
import pl.lightbulb.customer_panel.PhotoService;
import pl.lightbulb.customer_panel.User;
import pl.lightbulb.customer_panel.UserService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class SheetsController {

    private static final String APPLICATION_NAME = "Ligtbulb";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";


    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/static/credentials.json";

    private final SheetsIdService sheetsIdService;
    private final PhotoService photoService;
    private final UserService userService;

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = pl.lightbulb.customer_panel.apisheets.SheetsController.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    @GetMapping("/getsheet/{range}")
    public String getSheet(Model model, @PathVariable String range) throws IOException, GeneralSecurityException {

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = sheetsIdService.get(1L).get().getSpreadsheetId();

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        ValueRange valueResponse = service.spreadsheets().values().get(spreadsheetId, range).execute();

        List<List<Object>> values = valueResponse.getValues();

        model.addAttribute("values", values);

        User user = userService.findByRange(range);

        model.addAttribute("id", user.getId());

        return "sheets";
    }

    @GetMapping("/sheetsId")
    public String addSheetsId(Model model){
        model.addAttribute("sheetId", new SheetsId());
        return "sheetsId";
    }

    @PostMapping("/sheetIdUpdate")
    public String updateSheetsId(@ModelAttribute("spreadsheetId") String spreadsheetId, BindingResult bindingResult){
        SheetsId sheetsId = sheetsIdService.get(1L).get();
        sheetsId.setSpreadsheetId(spreadsheetId);
        sheetsIdService.update(sheetsId);
        return "sheets";
    }

//"1yOFS8du3Cikw2Yat9bf-2maKTFgyTkrwh-IEB3lfqXk"


}
