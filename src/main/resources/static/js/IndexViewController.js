import * as API from './AcftManagerServiceAPI.js';


//==============    Exported Functions   ================

export async function createNewSoldierController(){
    const host = getHost();

    const outputText = document.getElementById('displayText'); //DOM Obj
    const messageText = document.getElementById('messageText');

    const displayErrorMessage = () => {
        outputText.textContent = null;
        messageText.textContent = 'Age cannot be negative'
    };

    outputText.textContent = null;
    messageText.textContent = null;
    const testGroup = document.getElementById('existingTestGroups'); //DOM Obj
    const userPasscode = sessionStorage.getItem('userPasscode');
    let lastName = document.getElementById('lastNameField'); //DOM Obj
    let firstName = document.getElementById('firstNameField'); //DOM Obj
    let age = document.getElementById('ageField'); //DOM Obj
    let gender = document.getElementById('genderField') //DOM Obj
    let response = null;
    if (lastName.value.length == 0 || firstName.value.length == 0 || age.value.length == 0){
        outputText.innerHTML = 'One or more required fields are empty';
        return;
    } 
    if (age.value < 0){
        displayErrorMessage();
        return;
    }
    else if (userPasscode !== null){
        try {
            response = await API.createNewSoldier(testGroup.value, 
                lastName.value, 
                firstName.value, 
                age.value, 
                gender.value,
                userPasscode,
                host); //Number
            } catch (error){
                console.log(error);
                displayAccessUnauthorizedMessage();
                return;
            }
    }
    else {
        try {
            response = await API.createNewSoldier(testGroup.value, 
                                                        lastName.value, 
                                                        firstName.value, 
                                                        age.value, 
                                                        gender.value, 
                                                        undefined,
                                                        host); //Number
            outputText.innerHTML = `New soldier has ID ${response}`;
            lastName.value = null;
            firstName.value = null;
            age.value = null;
        } catch (error){
            console.log(error);
            displayAccessUnauthorizedMessage();
            return;
        }
    }
    outputText.innerHTML = `New soldier has ID ${response}`;
    lastName.value = null;
    firstName.value = null;
    age.value = null;   
}

export async function downloadTestGroupDataController(){
    const host = getHost();
    const existingTestGroups = document.getElementById('existingTestGroups');
    const errorText = document.getElementById('messageText');
    const userPasscode = (sessionStorage.getItem('userPasscode') === null) ? undefined : sessionStorage.getItem('userPasscode');
    if (existingTestGroups.length === 0){
        errorText.textContent = "No available test groups";
        return;
    }
    const testGroupId = existingTestGroups.value;
    try {
        //returns blob
        let response = await API.downloadTestGroupData(testGroupId, userPasscode, host);
        download(response, `test_group_${testGroupId}_data.xlsx`);
    } catch (error) {
        console.log(error);
        displayAccessUnauthorizedMessage()
    }
}

export async function showEditSoldierDataViewController(){
    const host = getHost();
    if (document.getElementById('existingTestGroups') === null){
        API.getEditSoldierDataView(host);
        return;
    }
    const selectedTestGroupId = document.getElementById('existingTestGroups').value;
    try {
        await API.getTestGroupById(selectedTestGroupId, sessionStorage.getItem('userPasscode'), host);
    } catch (error) {
        console.log(error);
        document.getElementById('messageText').textContent = 'No available test groups or access to selected test group not authorized';
        return;    
    }
    sessionStorage.setItem('selectedTestGroupId', selectedTestGroupId);
    API.getEditSoldierDataView(host);
}

export async function showVisualizeTestDataViewController(){
    const host = getHost();
    if (sessionStorage.getItem('view') == '0') {
        const selectedTestGroupId = document.getElementById('existingTestGroups').value;
        try {
            await API.getTestGroupById(selectedTestGroupId, sessionStorage.getItem('userPasscode'), host);
        } catch (error) {
            console.log(error);
            document.getElementById('messageText').textContent = 'No available test groups or access to selected test group not authorized';
            return;    
        }
        sessionStorage.setItem('selectedTestGroupId', selectedTestGroupId);
    }
    API.getVisualizeTestDataView(host);
}

export function showAboutViewController(){
    const host = getHost();
    if (sessionStorage.getItem('view') == '0') {
        sessionStorage.setItem('selectedTestGroupId', document.getElementById('existingTestGroups').value);
    }
    API.getAboutView(host);
}

export async function executePasscodeAction(){
    const selectorValue = document.getElementById('passcodeActionSelector').value;
    switch(selectorValue){
        case '0':
            storeUserPasscode();
            break;
        case '1':
            showUserPasscode();
            break;
        case '2':
            await createNewTestGroupController();
            break;
        default: break;
    }
}

export async function executeAdminAction(){
    const selectorValue = document.getElementById('adminActionSelector').value;
    switch(selectorValue){
        case '0':
            await populateDatabase();
            break;
        case '1':
            await flushDatabase();
            document.getElementById('existingTestGroups').length = 0;
            break;
        default: break;
    }
}

export async function indexOnLoad(){
    await getAllTestGroupsController();
    const selectedTestGroupId = sessionStorage.getItem('selectedTestGroupId');
    if (selectedTestGroupId !== null){
        document.getElementById('existingTestGroups').value = selectedTestGroupId;
    }
    populateDataBaseSizeFieldController();
    showAddIndividualSoldierInterface();
    sessionStorage.setItem('view', '0');
}

export function adminActionSelectorOnChange(){
    populateDataBaseSizeFieldController();
}

export function getHost(){
    return location.protocol + '//' + location.host;
}

export function addSoldierInterfaceController(){
    const methodSelectorValue = document.getElementById('addSoldierMethodSelector').value;
    switch (methodSelectorValue){
        case '1':
            showAddIndividualSoldierInterface();
            break;
        case '2':
            showBulkUploadInterface();
            break;
        default: break;
    }
}

export function bulkSoldierUploadController(){
    const uploadElement = document.getElementById('uploadElement');
    
}

//==============    Component Functions   ================

function displayAccessUnauthorizedMessage(){
    document.getElementById('messageText').textContent = "Stored passcode invalid for selected test group";
}

async function populateDatabase(){
    const host = getHost();
    const sizeField = document.getElementById('populateDatabaseSizeField');
    const size = (sizeField.value.length === 0) ? 5 : sizeField.value;
    let testGroupId = await API.populateDatabase(size, host);
    //clear
    document.getElementById('existingTestGroups').length = 0;
    sizeField.value = null;
    //repopulate
    await getAllTestGroupsController();
    return testGroupId;
}

function download(blob, filename) {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download =  filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
}

function storeUserPasscode(){
    const messageText = document.getElementById('messageText');
    messageText.textContent = null;
    document.getElementById('displayText').textContent = null;
    const passcodeInputField = document.getElementById('createTestGroupPasscodeField');
    if (passcodeInputField.value == ""){
        messageText.textContent = "Passcode cannot be null"
        return;
    }
    sessionStorage.setItem('userPasscode', passcodeInputField.value);
    passcodeInputField.value = null;
}

function showUserPasscode(){
    const messageText = document.getElementById('messageText');
    const displayText = document.getElementById('displayText');
    messageText.textContent = null;
    displayText.textContent = null;
    if (sessionStorage.getItem('userPasscode') !== null) displayText.textContent = `User Passcode: ${sessionStorage.getItem('userPasscode')}`;
    else messageText.textContent = "User passcode is empty"
}

async function flushDatabase(){
    const host = getHost();
    let response;
    try{
        response = await API.flushDatabase(host);
    } catch (error){
        console.log(error);
    }
    if (response === false){
        document.getElementById('messageText').textContent = "Unsuccessful database flush";
    } else {
        document.getElementById('displayText').textContent = "Database flushed successfully";
    }

}

async function getAllTestGroupsController(){
    const host = getHost();
    let dropDownMenu = document.getElementById('existingTestGroups');
    let testGroupIdArray;
    try{
        testGroupIdArray = await API.getAllTestGroupIds(host); //{Number}
    } catch (error){
        console.log(error);
        return;
    }
    testGroupIdArray.forEach((id) => {
        let element = document.createElement("option");
        element.textContent = id;
        element.value = id;
        dropDownMenu.appendChild(element);
    });
    dropDownMenu.selectedIndex = dropDownMenu.length - 1;
}

async function createNewTestGroupController(){
    const host = getHost();
    const passcodeInput = document.getElementById('createTestGroupPasscodeField');
    const outputText = document.getElementById('displayText'); //DOM Obj
    outputText.textContent = "";
    const errorText = document.getElementById('messageText');
    errorText.textContent = "";
    let response;
    if (passcodeInput.value.length == 0){
        response = await API.createNewTestGroup(undefined, host);
        errorText.textContent = "WARNING: Test Group created without passcode";
        outputText.innerHTML = `New testgroup has ID ${response}`;
    } else{
        response = await API.createNewTestGroup(passcodeInput.value, host);
        outputText.innerHTML = `New testgroup has ID ${response} and passcode ${passcodeInput.value}`;
    }
    const dropDownMenu = document.getElementById('existingTestGroups');
    const element = document.createElement("option");
    element.textContent = response;
    element.value = response;
    dropDownMenu.appendChild(element);
    passcodeInput.value = null;
    dropDownMenu.selectedIndex = dropDownMenu.length-1;
}

function showPopulateDatabaseSizeField(){
    const anchorPoint = document.getElementById('adminActionDiv');
    const input = document.createElement('input');
    input.type = 'number';
    input.className = 'field smallNumberField';
    input.id = 'populateDatabaseSizeField'
    const label = document.createElement('label');
    label.for = input.id;
    label.textContent = 'Population Size: ';
    label.id = 'populateDatabaseSizeFieldLabel'
    anchorPoint.appendChild(label);
    anchorPoint.appendChild(input);
}

function hidePopulateDatabaseSizeField(){
    const anchorPoint = document.getElementById('adminActionDiv');
    const input = document.getElementById('populateDatabaseSizeField');
    const label = document.getElementById('populateDatabaseSizeFieldLabel');
    if (input !== null) anchorPoint.removeChild(input);
    if (label !== null) anchorPoint.removeChild(label);
}

function populateDataBaseSizeFieldController(){
    const value = document.getElementById('adminActionSelector').value;
    switch (value){
        case '0':
            showPopulateDatabaseSizeField();
            break;
        case '1':
            hidePopulateDatabaseSizeField();
        default: break;
    }
}


function showAddIndividualSoldierInterface(){
    const anchorPoint = document.getElementById('addSoldierDiv');
    while (anchorPoint.childElementCount > 0) anchorPoint.removeChild(anchorPoint.lastChild);
    const createNewSoldierButton = document.getElementById('createNewSoldierButton');
    createNewSoldierButton.textContent = 'Add Soldier';
    createNewSoldierButton.addEventListener('click', createNewSoldierController);
    let genderOptionState = true;
    const elementTypeArray = ['label', 'input', 'label', 'input', 'label', 'input', 'label', 'select', 'option', 'option'];
    const forAttributeArray = ['lastNameField', null, 'firstNameField', null, 'ageField', null, 'genderField', null, null, null];
    const classAttributeArray = [null, 'field', null, 'field', null, 'field', null, 'dropdown', null, null];
    const textContentArray = ['Last Name:', null, 'First Name:', null, 'Age:', null, 'Gender: ', null, 'Male', 'Female'];
    const idArray = [null, 'lastNameField', null, 'firstNameField', null, 'ageField', null, 'genderField', null, null];
    const valueTypeArray = [null, 'text', null, 'text', null, 'number', null, null, null, null];
    for (let i = 0; i < 10; i++){
        const element = document.createElement(elementTypeArray[i]);
        if (forAttributeArray[i] !== null) element.htmlFor = forAttributeArray[i];
        if (classAttributeArray[i] !== null) element.className = classAttributeArray[i];
        if (textContentArray[i] !== null) element.textContent = textContentArray[i];
        if (idArray[i] !== null) element.id = idArray[i];
        if (valueTypeArray[i] !== null) element.setAttribute('type', valueTypeArray[i]);
        if (element.tagName === 'OPTION'){
            element.value = genderOptionState;
            genderOptionState = !genderOptionState;
            document.getElementById(idArray[7]).appendChild(element);
        } else {
            anchorPoint.appendChild(element);
            if (element.tagName !== 'LABEL'){
                anchorPoint.appendChild(document.createElement('br'));
            }
        }
    }
}

function showBulkUploadInterface(){
    const bulkUploadButton = document.getElementById('createNewSoldierButton');
    bulkUploadButton.textContent = 'Upload Soldiers';
    bulkUploadButton.removeEventListener('click', createNewSoldierController);
    bulkUploadButton.addEventListener('click', bulkUploadController);
    const anchorPoint = document.getElementById('addSoldierDiv');
    while (anchorPoint.childElementCount > 0) anchorPoint.removeChild(anchorPoint.lastChild);
    const templateDownloadButton = document.createElement('button');
    templateDownloadButton.id = 'templateDownloadButton';
    templateDownloadButton.className = 'button';
    templateDownloadButton.textContent = 'Download Template';
    templateDownloadButton.addEventListener('click', downloadTemplateController);
    anchorPoint.appendChild(templateDownloadButton);
    const uploadElement = document.createElement('input');
    uploadElement.id = 'uploadElement';
    uploadElement.className = 'field';
    uploadElement.setAttribute('type', 'file');
    anchorPoint.appendChild(uploadElement);
}

async function bulkUploadController(){
    console.log('bulkUploadController called');
}

async function downloadTemplateController(){
    const host = getHost();
    try {
        let response = await API.getBulkUploadTemplate(host);
        download(response, 'bulkUploadTemplate.xlsx');
    } catch (error) {
        console.log(error);
    }
    
}
