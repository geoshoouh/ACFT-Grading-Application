import * as API from './AcftManagerServiceAPI.js';

//**************** REST Functions ************************************
export async function createNewTestGroupController(){
    let outputText = document.getElementById('displayText'); //DOM Obj
    let response = await API.createNewTestGroup(); //Long
    let dropDownMenu = document.getElementById('existingTestGroups');
    let element = document.createElement("option");
    element.textContent = response;
    element.value = response;
    dropDownMenu.appendChild(element);
    outputText.innerHTML = `New testgroup has ID ${response}`;
}

export async function createNewSoldierController(){
    let outputText = document.getElementById('displayText'); //DOM Obj
    let testGroup = document.getElementById('existingTestGroups'); //DOM Obj
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

export async function getAllTestGroupsController(){
    let dropDownMenu = document.getElementById('existingTestGroups');
    let testGroupIdArray;
    try{
        testGroupIdArray = await API.getAllTestGroups(); //{Number}
    } catch (error){
        console.log(error);
    }
    testGroupIdArray.forEach((id) => {
        let element = document.createElement("option");
        element.textContent = id;
        element.value = id;
        dropDownMenu.appendChild(element);
    });
}

export async function updateSoldierScoreController(){
    const soldierId = parseInt(document.getElementById('idsInTestGroup').value);
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
            if (mdlScore < 0) diplayErrorMessage();
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
export async function populateSoldiersByTestGroupIdController(){
    let testGroupMenu = document.getElementById('existingTestGroups');
    if (testGroupMenu.length == 0){
        document.getElementById('messageText').innerHTML = "No available test groups";
        return;
    }
    let testGroup;
    try {
        testGroup = await API.getTestGroupById(testGroupMenu.value);
    } catch(error){
        console.log(error);
    }
    let soldierIdArray = testGroup.soldierPopulation;
    if (soldierIdArray.length == 0) return;
    let soldierMenu = document.getElementById('idsInTestGroup');
    soldierMenu.length = 0;
    soldierIdArray.forEach((soldier) => {
        let element = document.createElement('option');
        element.textContent = soldier.id;
        element.value = soldier.id;
        soldierMenu.appendChild(element);
    });
}

export async function showEditSoldierDataViewController(){
    await API.getEditSoldierDataView();
}

export async function getHomePageViewController(){
    await API.getHomePageView();
}

export async function displaySoldierName(){
    const output = document.getElementById('displaySoldierName');
    const messageText = document.getElementById('messageText');
    const testGroups = document.getElementById('existingTestGroups');
    const soldierId = document.getElementById('idsInTestGroup');
    if (testGroups.length == 0){
        messageText.innerHTML = 'No available test groups';
        return;
    }
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

export async function getTestGroupByIdController(){
    let testGroup;
    let testGroupId = document.getElementById('existingTestGroups').value;
    try{
        testGroup = await API.getTestGroupById(testGroupId);
        return testGroup;
    } catch (error){
        console.log(error);
    }
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
    await getAllTestGroupsController();
    await populateSoldiersByTestGroupIdController();
    eventInputController();
}

export async function indexOnLoad(){
    getAllTestGroupsController();
}




