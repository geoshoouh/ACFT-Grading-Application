import * as API from './AcftManagerServiceAPI.js';

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
    for (let i = 0; i < soldierMenu.length; i++){
        soldierMenu.remove(0);
    }
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
    let output = document.getElementById('displaySoldierName');
    let messageText = document.getElementById('messageText');
    let testGroups = document.getElementById('existingTestGroups');
    let soldierId = document.getElementById('idsInTestGroup');
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



