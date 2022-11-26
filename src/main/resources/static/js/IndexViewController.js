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
    sessionStorage.setItem('view', '0');
}

export function adminActionSelectorOnChange(){
    populateDataBaseSizeFieldController();
}

export function getHost(){
    return location.protocol + '//' + location.host;
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
