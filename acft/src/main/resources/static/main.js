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
//**************** REST Functions ************************************



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
//**************** UI Functions ************************************** 



//*******Generate input fields by Event Type**********



export function generateInputForTimedEvents(){
    const minutesId = 'minutes';
    const minutes = document.createElement('input');
    minutes.type = 'number';
    minutes.id = minutesId;
    const secondsId = 'seconds';
    const seconds = document.createElement('input');
    seconds.type = 'number';
    seconds.id = secondsId;
    const targetElement = document.getElementById('soldierIdAndEventSelectionDiv');
    targetElement.append("Minutes: ");
    targetElement.appendChild(minutes);
    targetElement.append(" Seconds: ");
    targetElement.appendChild('seconds');
}

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



//******* Setup function  ****************************
export async function onload(){
    await getAllTestGroupsController();
    await populateSoldiersByTestGroupIdController();
    eventInputController();
}




