import { createNewSoldier, createNewTestGroup } from './AcftManagerServiceAPI.js';

export async function createNewTestGroupController(){
    let outputText = document.getElementById('displayText');
    let response = await createNewTestGroup();
    outputText.innerHTML = `New testgroup has ID ${response}`;
}

export async function createNewSoldierController(){
    let outputText = document.getElementById('displayText');
    let lastName = document.getElementById('lastNameField').value;
    let firstName = document.getElementById('firstNameField').value;
    let age = document.getElementById('ageField').value;
    let gender = document.getElementById('genderField').value;
    if (lastName.length == 0 || firstName.length == 0 || age.length == 0){
        outputText.innerHTML = 'One or more required fields are empty';
    } else {
        let response = await createNewSoldier(1, 'Tate', 'Joshua', 26, true);
        outputText.innerHTML = `New soldier has ID ${response}`;
    }
}


