package space.hideaway.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import space.hideaway.model.Data;
import space.hideaway.model.Device;
import space.hideaway.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * HIDE CoCoTemp 2016
 * The class responsible for the parsing and upload of user-data.
 */
@Service
public class UploadService {

    @Autowired
    DeviceServiceImplementation deviceServiceImplementation;
    @Autowired
    UserServiceImplementation userService;
    /**
     * The file uploaded by the user.
     */
    private MultipartFile multipartFile;
    @Autowired
    private DataServiceImplementation dataServiceImplementation;
    @Autowired
    private SecurityService securityService;

    /**
     * Get the file uploaded by the user.
     *
     * @return The file uploaded by the user.
     */
    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    /**
     * Set the file uploaded by the user.
     *
     * @param multipartFile The new file uploaded by the user.
     * @return The UploadService class for method chaining.
     */
    public UploadService setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
        return this;
    }

    /**
     * Parse the file, line-by-line.
     * This method is performance critical as user data will be a CSV file MANY MANY lines. Time spent here is
     * time wasted.
     * TODO: possible application of multithreading.
     */
    public String parseFile(String deviceKey) {

        Device device = deviceServiceImplementation.findByKey(deviceKey);
        UUID deviceId = device.getId();
        Long userId = device.getUserId();
        ArrayList<Data> dataList = new ArrayList<>();
        ICsvBeanReader iCsvBeanReader = null;
        try {
            iCsvBeanReader = new CsvBeanReader(new FileReader(convertToFile()), CsvPreference.STANDARD_PREFERENCE);
            final CellProcessor[] cellProcessors = new CellProcessor[]{
                    new ParseDate("yyyy-MM-dd HH:mm:ss", true, Locale.ENGLISH),
                    new ParseDouble()
            };
            final String[] header = iCsvBeanReader.getHeader(true);
            Data dataBean;
            while ((dataBean = iCsvBeanReader.read(Data.class, header, cellProcessors)) != null) {
                dataBean.setDeviceID(deviceId);
                dataBean.setUserID(userId.intValue());
                dataList.add(dataBean);
            }
            dataServiceImplementation.batchSave(dataList);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (iCsvBeanReader != null) {
            try {
                iCsvBeanReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private boolean isCorrectUser(User user, String deviceKey) {
        boolean found = false;
        Set<Device> deviceSet = user.getDeviceSet();
        for (Device device : deviceSet) {
            if (device.getId().toString().equals(deviceKey)) {
                found = true;
            }
        }
        return found;
    }

    private File convertToFile() {
        File convertedFile = null;
        try {
            convertedFile = File.createTempFile("temp-upload", ".csv");
            FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
            fileOutputStream.write(multipartFile.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertedFile;
    }
}
