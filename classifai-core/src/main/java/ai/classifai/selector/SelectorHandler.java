/*
 * Copyright (c) 2020 CertifAI
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package ai.classifai.selector;

import ai.classifai.database.loader.LoaderStatus;
import ai.classifai.database.loader.ProjectLoader;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selector Handler for File & Folder Selector and Database Update
 *
 * @author ChiaWei
 */
@Slf4j
public class SelectorHandler {

    //key: (Integer) Project ID
    //value: (String) Project Name
    private static Map projectIDNameDict;

    //projectUUIDNameDict opposite
    //key: (String) Project Name
    //value: (Integer) Project ID
    private static Map projectNameIDDict;

    private static Map projectLoaderDict;

    private static AtomicInteger projectIDGenerator;

    @Getter private static String projectNameBuffer;

    private static boolean isWindowOpen = false;
    private static boolean isLoaderProcessing = false; // only one file/folder selector can open at one time. even for multiple projects.
    @Setter @Getter private static SelectorStatus selectorStatus;

    @Getter
    private static String currentWindowSelection;//FILE FOLDER

    @Getter
    private static final File rootSearchPath = new File(System.getProperty("user.home"));

    public static final String FILE = "file";
    public static final String FOLDER = "folder";


    static {
        projectIDNameDict = new HashMap<Integer, String>();
        projectNameIDDict = new HashMap<String, String>();
        projectLoaderDict = new HashMap<String, ProjectLoader>();

        projectIDGenerator = new AtomicInteger(0);
    }

    public static ProjectLoader getProjectLoader(String projectName) {

        if(projectLoaderDict.containsKey(projectName) == false)
        {
            log.error("Project name of " + projectName + " cannot be found in ProjectLoader. Critical Error.");
            return null;
        }

        return (ProjectLoader) projectLoaderDict.get(projectName);
    }

    public static boolean isWindowOpen() {
        return isWindowOpen;
    }

    public static void setProjectNameNID(@NonNull String projectName, @NonNull Integer projectID) {
        projectNameIDDict.put(projectName, projectID);
        projectIDNameDict.put(projectID, projectName);

        projectLoaderDict.put(projectName, new ProjectLoader());
    }

    public static boolean initSelector(String selection)
    {
        if((selection.equals(FILE)) || selection.equals(FOLDER))
        {
            currentWindowSelection = selection;
        }
        else
        {
            log.error("Current input selector not allowed: " + selection + ". Allowed parameters are file/folder");
            return false;
        }
        return true;
    }

    public static boolean isProjectNameRegistered(String projectName)
    {
        return projectNameIDDict.containsKey(projectName);
    }

    public static ProjectLoader getCurrentProjectLoader()
    {
        return getProjectLoader(projectNameBuffer);
    }

    public static Integer getProjectID(String projectName)
    {
        return (Integer) projectNameIDDict.get(projectName);
    }

    public static Integer getProjectIDFromBuffer()
    {
        return getProjectID(projectNameBuffer);
    }

    public static void setProjectNameBuffer(String projectName)
    {
        projectNameBuffer = projectName;
    }

    public static boolean isLoaderProcessing()
    {
        return isLoaderProcessing;
    }

    public static void configureOpenWindow(String projectName, Integer uuidGenerator)
    {
        ProjectLoader projectLoader = (ProjectLoader) projectLoaderDict.get(projectName);

        if(uuidGenerator == 0)
        {
            //prevent nan
            projectLoader.setProgressUpdate(new ArrayList<>(Arrays.asList(0, 1)));
        }
        else
        {
            projectLoader.setProgressUpdate(new ArrayList<>(Arrays.asList(0, uuidGenerator)));

        }

        isWindowOpen = true;
    }

    public static List<Integer> getProgressUpdate(String projectName)
    {
        ProjectLoader projectLoader = (ProjectLoader) projectLoaderDict.get(projectName);

        return projectLoader.getProgressUpdate();
    }

    public static void startDatabaseUpdate(@NonNull String projectName) {
        projectNameBuffer = projectName;
        selectorStatus = SelectorStatus.WINDOW_CLOSE_LOADING_FILES;
        getCurrentProjectLoader().setLoaderStatus(LoaderStatus.LOADING);
        isLoaderProcessing = true;
        isWindowOpen = false;
    }

    public static void stopDatabaseUpdate()
    {
        ProjectLoader loader = getCurrentProjectLoader();
        if(loader.getSanityUUIDList().isEmpty())
        {
            loader.setLoaderStatus(LoaderStatus.EMPTY);
        }
        else
        {
            loader.setLoaderStatus(LoaderStatus.LOADED);
        }

        isLoaderProcessing = false;
        projectNameBuffer = "";
    }
    /**
     * @param state true = open, false = close
     */
    public static void setWindowState(boolean state)
    {
        isWindowOpen = state;
    }

    public static Integer generateProjectID()
    {
        return projectIDGenerator.incrementAndGet();
    }

    public static void setProjectIDGenerator(Integer seedNumber)
    {
        projectIDGenerator = new AtomicInteger(seedNumber);
    }

}