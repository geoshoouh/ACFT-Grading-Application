import * as API from './AcftManagerServiceAPI.js';

//**************** REST Functions ************************************
export async function createNewTestGroupController(){
    const passcodeInput = document.getElementById('createTestGroupPasscodeField');
    const outputText = document.getElementById('displayText'); //DOM Obj
    outputText.textContent = "";
    const errorText = document.getElementById('messageText');
    errorText.textContent = "";
    let response;
    if (passcodeInput.value.length == 0){
        response = await API.createNewTestGroup();
        errorText.textContent = "WARNING: Test Group created without passcode";
        outputText.innerHTML = `New testgroup has ID ${response}`;
    } else{
        response = await API.createNewTestGroupWithPasscode(passcodeInput.value);
        outputText.innerHTML = `New testgroup has ID ${response} and passcode ${passcodeInput.value}`;
    }
    const dropDownMenu = document.getElementById('existingTestGroups');
    const element = document.createElement("option");
    element.textContent = response;
    element.value = response;
    dropDownMenu.appendChild(element);
    passcodeInput.value = null;
}

export async function createNewSoldierController(){
    const outputText = document.getElementById('displayText'); //DOM Obj
    const messageText = document.getElementById('messageText');
    outputText.textContent = null;
    messageText.textContent = null;
    const testGroup = document.getElementById('existingTestGroups'); //DOM Obj
    let authorizationResponse =  null;
    try {
        authorizationResponse = await API.getTestGroupById(testGroup.value, sessionStorage.getItem('userPasscode'));
    } catch (error) {
        displayAccessUnauthorizedMessage();
        console.log(error);
        return;
    }
    if (authorizationResponse.status === 500){
        displayAccessUnauthorizedMessage();
        return;
    }
    
    let lastName = document.getElementById('lastNameField'); //DOM Obj
    let firstName = document.getElementById('firstNameField'); //DOM Obj
    let age = document.getElementById('ageField'); //DOM Obj
    let gender = document.getElementById('genderField') //DOM Obj
    if (lastName.value.length == 0 || firstName.value.length == 0 || age.value.length == 0){
        outputText.innerHTML = 'One or more required fields are empty';
    } else {
        let response = await API.createNewSoldier(testGroup.value, 
                                                    lastName.value, 
                                                    firstName.value, 
                                                    age.value, 
                                                    gender.value); //Number
        outputText.innerHTML = `New soldier has ID ${response}`;
        lastName.value = null;
        firstName.value = null;
        age.value = null;
    }
}

export async function getTestGroupByIdController(){
    let testGroup;
    let testGroupId = document.getElementById('existingTestGroups').value;
    try{
        testGroup = await API.getTestGroupById(testGroupId);
    } catch (error){
        console.log(error);
        return;
    }
    return testGroup;
}

export async function getAllTestGroupsController(){
    let dropDownMenu = document.getElementById('existingTestGroups');
    let testGroupIdArray;
    try{
        testGroupIdArray = await API.getAllTestGroups(); //{Number}
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
}

export async function updateSoldierScoreController(){
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
    
    //Blocks are used here in some cases to reduce variable scope and prevent redifinition warnings
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
            let response = await API.updateSoldierScore(soldierId, eventId, mdlScore);
            if (response === undefined){
                displayHttpErrorMessage();
            }
            displayText.textContent = `Soldier with ID ${soldierId} received ${response} points for raw score of ${mdlScore}`;
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
            let response = await API.updateSoldierScore(soldierId, eventId, sptScore);
            if (response === undefined) displayHttpErrorMessage();
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
            let response = await API.updateSoldierScore(soldierId, eventId, hrpReps);
            if (response === undefined) displayHttpErrorMessage();
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

            if (minutes < 0 || seconds < 0 || seconds > 59){
                displayErrorMessage();
                break;
            }
            let response = await API.updateSoldierScore(soldierId, eventId, minutes * 60 + seconds);
            if (response === undefined) displayHttpErrorMessage();
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
    
                if (minutes < 0 || seconds < 0 || seconds > 59){
                    displayErrorMessage();
                    break;
                }
                let response = await API.updateSoldierScore(soldierId, eventId, minutes * 60 + seconds);
                if (response === undefined) displayHttpErrorMessage();
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
    
                if (minutes < 0 || seconds < 0 || seconds > 59){
                    displayErrorMessage();
                    break;
                }
                let response = await API.updateSoldierScore(soldierId, eventId, minutes * 60 + seconds);
                if (response === undefined) displayHttpErrorMessage();
                const secondsString = (seconds < 10) ? '0' + seconds : seconds;
                displayText.textContent = `Soldier with ID ${soldierId} received ${response} points for raw score of ${minutes}:${secondsString}`;
                }
            break;
        default: break;
    }
}
//*******************************************************************



//**************** UI Functions ************************************** 

export function displayAccessUnauthorizedMessage(){
    const view = sessionStorage.getItem('view');
    switch (view){
        case '0':
            document.getElementById('messageText').textContent = "Stored passcode invalid for selected test group";
        case '1':
            document.getElementById('errorText').textContent = "User passcode invalid for selected test group; cannot access soldier data"
        default:
            console.log('No unauthorized access behavior defined for the view value stored');
    }
    
}

export async function populateSoldiersByTestGroupIdController(){
    let testGroup = null;
    try {
        testGroup = await API.getTestGroupById(sessionStorage.getItem('selectedTestGroupId'), sessionStorage.getItem('userPasscode'));
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
}

export async function showEditSoldierDataViewController(){
    const selectedTestGroupId = document.getElementById('existingTestGroups').value;
    try {
        await API.getTestGroupById(selectedTestGroupId, sessionStorage.getItem('userPasscode'));
    } catch (error) {
        console.log(error);
        document.getElementById('messageText').textContent = 'No available test groups or access to selected test group not authorized';
        return;    
    }
    sessionStorage.setItem('selectedTestGroupId', selectedTestGroupId);
    await API.getEditSoldierDataView();
}

export async function getHomePageViewController(){
    await API.getHomePageView();
}

export async function displaySoldierName(){
    const output = document.getElementById('displaySoldierName');
    const messageText = document.getElementById('messageText');
    const soldierId = document.getElementById('soldierIdSelector');
    if (soldierId.length == 0){
        messageText.innerHTML = 'No available soldiers';
        return;
    }
    let soldier;
    soldier = await API.getSoldierById(soldierId.value);
    if (soldier.status === 404){
        messageText.innerHTML = 'No soldiers created for this test group';
        return;
    }
    let stringOutput = `Soldier: ${soldier.lastName}, ${soldier.firstName}`
    output.innerHTML = stringOutput;
}

export async function populateDatabase(){
    let n = 3;
    let groupIdArray = [];
    let lastNames = ["Smith", "Jones", "Samuels", "Smith", "Conway"];
    let firstNames = ["Jeff", "Timothy", "Darnell", "Fredrick", "Katherine"];
    let ages = [26, 18, 19, 30, 23];
    let genders = [true, true, true, true, false];
    for (let i = 0; i < n; i++) groupIdArray.push(await API.createNewTestGroup());
    let j = 0;
    for (let i = 0; i < lastNames.length; i++){
        await API.createNewSoldier(groupIdArray[j], lastNames[i], firstNames[i], ages[i], genders[i]);
        j = (j == 2) ? 0 : j+1;
    }
    getAllTestGroupsController();
}

export function storeUserPasscode(){
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

export function showUserPasscode(){
    const messageText = document.getElementById('messageText');
    const displayText = document.getElementById('displayText');
    messageText.textContent = null;
    displayText.textContent = null;
    const passcodeStorageElement = document.getElementById('passcodeStorage');
    if (passcodeStorageElement.var !== undefined) displayText.textContent = `User Passcode: ${passcodeStorageElement.var}`;
    else messageText.textContent = "User passcode is empty"
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




