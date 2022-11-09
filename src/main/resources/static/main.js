import * as API from './AcftManagerServiceAPI.js';

//**************** REST Functions ************************************
function getHost(){
    return location.protocol + '//' + location.host;
}

export async function createNewTestGroupController(){
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

export async function createNewSoldierController(){
    const host = getHost();
    const outputText = document.getElementById('displayText'); //DOM Obj
    const messageText = document.getElementById('messageText');
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

export async function getTestGroupByIdController(){
    const host = getHost();
    let testGroup;
    let testGroupId = document.getElementById('existingTestGroups').value;
    try{
        testGroup = await API.getTestGroupById(testGroupId, undefined, host);
    } catch (error){
        console.log(error);
        return;
    }
    return testGroup;
}

export async function getAllTestGroupsController(){
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

export async function updateSoldierScoreController(){
    const host = getHost();
    const soldierId = parseInt(document.getElementById('soldierIdSelector').value);
    const messageText = document.getElementById('messageText');
    const displayText = document.getElementById('displaySoldierName');
    messageText.textContent = '';
    if (soldierId.length == 0) messageText.textContent = 'No available soldiers';
    const eventId = parseInt(document.getElementById('eventSelector').value);

    const displayErrorMessage = () => {
        displayText.textContent = '';
        messageText.textContent = 'Invalid Input'
    };

    const displayHttpErrorMessage = () => {
        displayText.textContent = '';
        messageText.textContent = 'Server Error';
    };

    const userPasscode = sessionStorage.getItem('userPasscode');
    const nullUserPasscode = (userPasscode === null) ? true : false;
    
    //Blocks are used here in some cases to reduce variable scope and prevent redifinition warnings
    let response;
    switch (eventId){
        case 1:
            let mdlScoreString = document.getElementById('mdlInputField').value;
            if (mdlScoreString.length == 0){
                displayErrorMessage();
                break;
            }
            const mdlScore = parseInt(mdlScoreString);
            if (mdlScore < 0){
                displayErrorMessage();
                break;
            }
            try {
                response = (nullUserPasscode) ? await API.updateSoldierScore(soldierId, eventId, mdlScore, undefined, host) : await API.updateSoldierScore(soldierId, eventId, mdlScore, userPasscode, host);
            } catch (error){
                console.log(error);
                displayHttpErrorMessage();
                return;
            }
            displayText.textContent = `Soldier with ID ${soldierId} received ${response} points for raw score of ${mdlScore}`;
            break;
        case 2:
            {let sptMetersInput = document.getElementById('sptMetersInput');
            let sptMeterTenthsInput = document.getElementById('sptMeterTenthsInput');
            if (sptMetersInput.value.length == 0){
                displayErrorMessage();
                break;
            }
            const sptMeters = parseInt(sptMetersInput.value);
            const sptMeterTenths = (sptMeterTenthsInput.value.length > 0) ? parseInt(sptMeterTenthsInput.value) : 0;
            if (sptMeters < 0 || sptMeterTenths < 0 || sptMeterTenths > 9){
                displayErrorMessage();
                break;
            } 
            const sptScore = sptMeters * 10 + sptMeterTenths;
            try {
                response = (nullUserPasscode) ? await API.updateSoldierScore(soldierId, eventId, sptScore, undefined, host) : await API.updateSoldierScore(soldierId, eventId, sptScore, userPasscode, host);
            } catch (error){
                console.log(error);
                displayHttpErrorMessage();
                return;
            }
            displayText.textContent = `Soldier with ID ${soldierId} received ${response} points for raw score of ${sptMeters}.${sptMeterTenths}`;}
            break;
        case 3:
            {let hrpInput = document.getElementById('hrpInputField');
            if (hrpInput.value.length == 0){
                displayErrorMessage();
                break;
            }
            const hrpReps = parseInt(hrpInput.value);
            if (hrpReps < 0){
                displayErrorMessage();
                break;
            }
            try {
                response = (nullUserPasscode) ? await API.updateSoldierScore(soldierId, eventId, hrpReps, undefined, host) : await API.updateSoldierScore(soldierId, eventId, hrpReps, userPasscode, host);
            } catch (error){
                console.log(error);
                displayHttpErrorMessage();
                return;
            }
            displayText.textContent = `Soldier with ID ${soldierId} received ${response} points for raw score of ${hrpReps}`;}
            break;
        case 4:
            {
            let minutesInput = document.getElementById('minutesInput');
            let secondsInput = document.getElementById('secondsInput');
            if (minutesInput.value.length == 0){
                displayErrorMessage();
                break;
            }
            const minutes = parseInt(minutesInput.value);
            const seconds = (secondsInput.value.length > 0) ? parseInt(secondsInput.value) : 0;
            const sdcScore = minutes * 60 + seconds;
            if (minutes < 0 || seconds < 0 || seconds > 59){
                displayErrorMessage();
                break;
            }
            try {
                response = (nullUserPasscode) ? await API.updateSoldierScore(soldierId, eventId, sdcScore, undefined, host) : await API.updateSoldierScore(soldierId, eventId, sdcScore, userPasscode, host);
            } catch (error){
                console.log(error);
                displayHttpErrorMessage();
                return;
            }
            const secondsString = (seconds < 10) ? '0' + seconds : seconds;
            displayText.textContent = `Soldier with ID ${soldierId} received ${response} points for raw score of ${minutes}:${secondsString}`;
            }
            break;
        case 5:
            {
                let minutesInput = document.getElementById('minutesInput');
                let secondsInput = document.getElementById('secondsInput');
                if (minutesInput.value.length == 0){
                    displayErrorMessage();
                    break;
                }
                const minutes = parseInt(minutesInput.value);
                const seconds = (secondsInput.value.length > 0) ? parseInt(secondsInput.value) : 0;
                const plkScore = minutes * 60 + seconds;
                if (minutes < 0 || seconds < 0 || seconds > 59){
                    displayErrorMessage();
                    break;
                }
                try {
                    response = (nullUserPasscode) ? await API.updateSoldierScore(soldierId, eventId, plkScore, undefined, host) : await API.updateSoldierScore(soldierId, eventId, plkScore, userPasscode, host);
                } catch (error){
                    console.log(error);
                    displayHttpErrorMessage();
                    return;
                }
                const secondsString = (seconds < 10) ? '0' + seconds : seconds;
                displayText.textContent = `Soldier with ID ${soldierId} received ${response} points for raw score of ${minutes}:${secondsString}`;
                }
            break;
        case 6:
            {
                let minutesInput = document.getElementById('minutesInput');
                let secondsInput = document.getElementById('secondsInput');
                if (minutesInput.value.length == 0){
                    displayErrorMessage();
                    break;
                }
                const minutes = parseInt(minutesInput.value);
                const seconds = (secondsInput.value.length > 0) ? parseInt(secondsInput.value) : 0;
                const tmrScore = minutes * 60 + seconds;
                if (minutes < 0 || seconds < 0 || seconds > 59){
                    displayErrorMessage();
                    break;
                }
                try {
                    response = (nullUserPasscode) ? await API.updateSoldierScore(soldierId, eventId, tmrScore, undefined, host) : await API.updateSoldierScore(soldierId, eventId, tmrScore, userPasscode, host);
                } catch (error){
                    console.log(error);
                    displayHttpErrorMessage();
                    return;
                }
                const secondsString = (seconds < 10) ? '0' + seconds : seconds;
                displayText.textContent = `Soldier with ID ${soldierId} received ${response} points for raw score of ${minutes}:${secondsString}`;
                }
            break;
        default: break;
    }
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

export async function flushDatabase(){
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

//*******************************************************************



//**************** UI Functions ************************************** 

export function displayAccessUnauthorizedMessage(){
    const view = sessionStorage.getItem('view');
    switch (view){
        case '0':
            document.getElementById('messageText').textContent = "Stored passcode invalid for selected test group";
            break;
        case '1':
            document.getElementById('errorText').textContent = "User passcode invalid for selected test group; cannot access soldier data"
            break;
        default:
            console.log('No unauthorized access behavior defined for the view value stored');
    }
    
}

export async function populateSoldiersByTestGroupIdController(){
    const host = getHost();
    let testGroup = null;
    try {
        testGroup = await API.getTestGroupById(sessionStorage.getItem('selectedTestGroupId'), sessionStorage.getItem('userPasscode'), host);
    } catch(error){
        console.log(error);
        return;
    }
    let soldierIdArray = testGroup.soldierPopulation;
    if (soldierIdArray.length == 0) return;
    let soldierMenu = document.getElementById('soldierIdSelector');
    soldierMenu.length = 0;
    soldierIdArray.forEach((soldier) => {
        let element = document.createElement('option');
        element.textContent = soldier.id;
        element.value = soldier.id;
        soldierMenu.appendChild(element);
    });
    getAllTestGroupsController();
}

export async function showEditSoldierDataViewController(){
    const host = getHost();
    const selectedTestGroupId = document.getElementById('existingTestGroups').value;
    try {
        await API.getTestGroupById(selectedTestGroupId, sessionStorage.getItem('userPasscode'), host);
    } catch (error) {
        console.log(error);
        document.getElementById('messageText').textContent = 'No available test groups or access to selected test group not authorized';
        return;    
    }
    sessionStorage.setItem('selectedTestGroupId', selectedTestGroupId);
    await API.getEditSoldierDataView(host);
}

export async function getHomePageViewController(){
    const host = getHost();
    await API.getHomePageView(host);
}

export async function displaySoldierName(){
    const host = getHost();
    const output = document.getElementById('displaySoldierName');
    const messageText = document.getElementById('messageText');
    const soldierId = document.getElementById('soldierIdSelector');
    if (soldierId.length == 0){
        messageText.innerHTML = 'No available soldiers';
        return;
    }
    let soldier;
    soldier = await API.getSoldierById(soldierId.value, sessionStorage.getItem('userPasscode'), host);
    if (soldier.status === 404){
        messageText.innerHTML = 'No soldiers created for this test group';
        return;
    }
    let stringOutput = `Soldier: ${soldier.lastName}, ${soldier.firstName}`
    output.innerHTML = stringOutput;
}

function generateRandomRawScore(eventId){
    //follows front-end 1-indexing convention
    let floor = 0;
    let ceiling = 0;
        switch (eventId){
            case 1:
                floor = 120;
                ceiling = 340;
                break;
            case 2:
                floor = 39;
                ceiling = 130;
                break;
            case 3:
                floor = 10;
                ceiling = 61;
                break;
            case 4:
                floor = 89;
                ceiling = 300;
                break;
            case 5:
                floor = 70;
                ceiling = 220;
                break;
            case 6:
                floor = 780;
                ceiling = 1500;
                break;
            default: break;
        }
        return Math.floor(Math.random() * (ceiling - floor)) + floor;
}

export async function populateDatabase(){
    const host = getHost();
    let testGroupId = await API.createNewTestGroup(undefined, host);
    const n = 5;
    let soldierIds = new Array(n);
    const lastNames = ["Smith", "Jones", "Samuels", "Smith", "Conway"];
    const firstNames = ["Jeff", "Timothy", "Darnell", "Fredrick", "Katherine"];
    const ages = [26, 18, 19, 30, 23];
    const genders = [true, true, true, true, false];
    for (let i = 0; i < n; i++){
        soldierIds[i] = await API.createNewSoldier(testGroupId, lastNames[i], firstNames[i], ages[i], genders[i], undefined, host);
        //In front-end, event IDs are 1-indexed
        //Client-side API takes 1-indexed and decrements it before sending request
        for (let j = 1; j <= 6; j++){
            await API.updateSoldierScore(soldierIds[i], j, generateRandomRawScore(j), undefined, host);
        }
    }
    //clear
    document.getElementById('existingTestGroups').length = 0;
    //repopulate
    await getAllTestGroupsController();
    return testGroupId;
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
//******************************************************************** 



//*******Generate input fields by Event Type**********
export function eventInputController(){
    const eventId =  parseInt(document.getElementById('eventSelector').value);
    const targetElement = document.getElementById('soldierIdAndEventSelectionDiv');
    if (targetElement.childElementCount > 0) targetElement.removeChild(targetElement.firstChild);
    targetElement.textContent = '';
    switch (eventId){
        case 1:
            const mdlInput = document.createElement('input');
            mdlInput.type = 'number';
            mdlInput.id = 'mdlInputField';
            targetElement.appendChild(mdlInput);
            targetElement.append("  [ lbs ]");
            break;
        case 2:
            const meters  = document.createElement('input');
            meters.type = 'number';
            meters.id = 'sptMetersInput';
            const meterTenths = document.createElement('input');
            meterTenths.type = 'number';
            meterTenths.id = 'sptMeterTenthsInput';
            targetElement.append("Meters: ");
            targetElement.appendChild(meters);
            targetElement.append(" Meter Tenths: ");
            targetElement.appendChild(meterTenths);
            break;
        case 3:
            const hrpInput = document.createElement('input');
            hrpInput.type = 'number';
            hrpInput.id = 'hrpInputField';
            targetElement.appendChild(hrpInput);
            targetElement.append("  [ repetitions ]");
            break;
        //Placed into blocks to circumvent redefinition warning
        case 4:
            {
            const minutes = document.createElement('input');
            minutes.type = 'number';
            minutes.id = 'minutesInput'
            const seconds = document.createElement('input');
            seconds.type = 'number';
            seconds.id = 'secondsInput';
            targetElement.append("Minutes: ");
            targetElement.appendChild(minutes);
            targetElement.append(" Seconds: ");
            targetElement.appendChild(seconds);
            }
            break;
        case 5:
            {
            const minutes = document.createElement('input');
            minutes.type = 'number';
            minutes.id = 'minutesInput'
            const seconds = document.createElement('input');
            seconds.type = 'number';
            seconds.id = 'secondsInput';
            targetElement.append("Minutes: ");
            targetElement.appendChild(minutes);
            targetElement.append(" Seconds: ");
            targetElement.appendChild(seconds);
            }
            break;
        case 6:
            const minutes = document.createElement('input');
            minutes.type = 'number';
            minutes.id = 'minutesInput'
            const seconds = document.createElement('input');
            seconds.type = 'number';
            seconds.id = 'secondsInput';
            targetElement.append("Minutes: ");
            targetElement.appendChild(minutes);
            targetElement.append(" Seconds: ");
            targetElement.appendChild(seconds);
            break;
        default: break;
    }
}
//****************************************************



//******* Setup functions  ****************************
export async function editSoldierDataViewOnLoad(){
    await populateSoldiersByTestGroupIdController();
    eventInputController();
    document.getElementById('testGroupText').textContent = `Editing data in test group ${sessionStorage.getItem('selectedTestGroupId')}`;
    sessionStorage.setItem('view', '1');
    if (document.getElementById('soldierIdSelector').length === 0) document.getElementById('testGroupText').textContent = `No soldiers in test group ${sessionStorage.getItem('selectedTestGroupId')}`;
}

export async function indexOnLoad(){
    await getAllTestGroupsController();
    sessionStorage.setItem('view', '0');
}




