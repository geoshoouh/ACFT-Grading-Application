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
    let testGroupIdArray = await API.getAllTestGroups(); //{Number}
    testGroupIdArray.forEach((id) => {
        let element = document.createElement("option");
        element.textContent = id;
        element.value = id;
        dropDownMenu.appendChild(element);
    });
}

export async function populateSoldiersByTestGroupIdController(){
    let testGroupMenu = document.getElementById('existingTestGroups');
    let soldierIdArray = await API.getSoldiersByTestGroupId(testGroupMenu.value);
    let soldierMenu = document.getElementById('idsInTestGroup');
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
    let soldierId = document.getElementById('idsInTestGroup').value;
    let soldier = await API.getSoldierById(soldierId);
    let stringOutput = `Soldier: ${soldier.lastName}, ${soldier.firstName}`
    output.innerHTML = stringOutput;
}






