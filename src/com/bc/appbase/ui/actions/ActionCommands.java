/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.appbase.ui.actions;

import com.bc.appcore.actions.ActionCommandsBase;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 8, 2017 10:12:10 PM
 */
public interface ActionCommands extends ActionCommandsBase {
    
    String UPDATE_DATABASE_WITH_ENTITIES = UpdateDatabaseWithEntities.class.getName();
    
    String VIEW_SUMMARY_REPORT = ViewSummaryReport.class.getName();
    
    String CHANGE_LOG_LEVEL = ChangeLogLevel.class.getName();
    
    String EXIT_UI_THEN_EXIT = ExitUIThenExit.class.getName();
    
    String REFRESH_ALL_RESULTS = RefreshAllResults.class.getName();
    
    String RELOAD_MAIN_RESULTS = ReloadMainResults.class.getName();

    String REFRESH_RESULTS = RefreshResults.class.getName();

    String VIEW_TABLE_AS_EXCEL = ViewTableAsExcel.class.getName();
    
    String CREATE_WORKSHEET_FRAME = CreateWorksheetFrame.class.getName();
    
    String PROMPT_SELECT_ROWS_AS_TABLE_MODEL = PromptSelectRowsAsTableModel.class.getName();
    
    String SAVE_TABLE_AS = SaveTableAs.class.getName();
    
    String SAVE_TABLE_MODEL = SaveTableModel.class.getName();
    
    String PRINT = Print.class.getName();
    
    String NEXT_RESULT = NextResult.class.getName();
    String PREVIOUS_RESULT = PreviousResult.class.getName();
    
    String FIRST_RESULT = FirstResult.class.getName();
    String LAST_RESULT = LastResult.class.getName();
    
    String SET_LOOK_AND_FEEL = SetLookAndFeel.class.getName();
    
    String DISPLAY_SETTINGS_UI = DisplaySettingsUI.class.getName();
    
    String UPDATE_SETTINGS_FROM_UI = UpdateSettingsFromUI.class.getName();
    
    String PROMPT_SELECT_EXCEL_FILE = PromptSelectExcelFile.class.getName();
    
    String PROMPT_SELECT_SHEETNAME = PromptSelectSheetName.class.getName();
    
    String PROMPT_SELECT_ENTITY_TYPE = PromptSelectEntityType.class.getName();
    
    String PROMPT_SELECT_SELECTION_TYPE = PromptSelectSelectionType.class.getName();
    
    String OPEN_FILE = OpenFile.class.getName();
    
    String DISPLAY_URL = DisplayURL.class.getName();
    
    String DISPLAY_TEXT = DisplayText.class.getName();
    
    String DISPLAY_OPEN_FOLDER_DIALOG = DisplayOpenFolderDialogAction.class.getName();
    
    String DISPLAY_OPEN_DIALOG = DisplayOpenDialog.class.getName();
    
    String DISPLAY_SAVE_DIALOG = DisplaySaveDialog.class.getName();
    
    String BLOCK_WINDOW_TILL_BUTTON_CLICK = BlockWindowTillButtonClick.class.getName();
    
    String BLOCK_WINDOW_TILL_CLOSE_BUTTON_CLICK = BlockWindowUntilCloseButtonClick.class.getName();
    
    String VIEW_LOG = ViewLog.class.getName();
    
    String DISPLAY_MULTIPLE_RECORDS = DisplaySelectedRecords.class.getName();
    
    String DISPLAY_RECORD_LIST = DisplayRecordList.class.getName();
    
    String DELETE_SELECTED_RECORDS = DeleteSelectedRecords.class.getName();

    String DISPLAY_ADD_SELECTION_TYPE_UI = DisplayAddSelectionTypeUI.class.getName();
    
    String DISPLAY_SELECTION_TYPE_TABLE = DisplaySelectionTypeTable.class.getName();
}
