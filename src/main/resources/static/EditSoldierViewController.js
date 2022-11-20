import * as API from './AcftManagerServiceAPI.js';


//==============    Exported Functions   ================

export async function updateSoldierScoreController(){
    const host = getHost();
    const soldierSelector = document.getElementById('soldierIdSelector');
    const errorText = document.getElementById('errorText');
    const displayText = document.getElementById('displaySoldierName');
    if (soldierSelector.length == 0){
        errorText.textContent = 'Cannot update scores with no soldiers in test group';
        return;
    }
    const soldierId = parseInt(soldierSelector.value);
    const eventId = parseInt(document.getElementById('eventSelector').value);

    const displayErrorMessage = () => {
        displayText.textContent = '';
        errorText.textContent = 'Invalid Input'
    };

    const displayHttpErrorMessage = () => {
        displayText.textContent = '';
        errorText.textContent = 'Server Error';
    };

    const userPasscode = sessionStorage.getItem('userPasscode');
    const nullUserPasscode = (userPasscode === null) ? true : false;
    
    //Blocks are used in switch statement as a quick-fix to prevent variable redifinition warnings
    let response;
    switch (eventId){
        case 1:
            const mdlScoreField = document.getElementById('mdlInputField');
            const mdlScoreString = mdlScoreField.value;
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
            mdlScoreField.value = null;
            displayText.textContent = `Soldier with ID ${soldierId} received ${response} points for raw score of ${mdlScore}`;
            break;
        case 2:
            {
            const sptMetersInput = document.getElementById('sptMetersInput');
            const sptMeterTenthsInput = document.getElementById('sptMeterTenthsInput');
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
            sptMetersInput.value = null;
            sptMeterTenthsInput.value = null;
            displayText.textContent = `Soldier with ID ${soldierId} received ${response} points for raw score of ${sptMeters}.${sptMeterTenths}`;}
            break;
        case 3:
            const hrpInput = document.getElementById('hrpInputField');
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
            hrpInput.value = null;
            displayText.textContent = `Soldier with ID ${soldierId} received ${response} points for raw score of ${hrpReps}`;
            break;
        case 4:
            const minutesInput = document.getElementById('minutesInput');
            const secondsInput = document.getElementById('secondsInput');
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
            minutesInput.value = null;
            secondsInput.value = null;
            const secondsString = (seconds < 10) ? '0' + seconds : seconds;
            displayText.textContent = `Soldier with ID ${soldierId} received ${response} points for raw score of ${minutes}:${secondsString}`;
            break;
        case 5:
            {
                const minutesInput = document.getElementById('minutesInput');
                const secondsInput = document.getElementById('secondsInput');
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
                minutesInput.value = null;
                secondsInput.value = null;
                const secondsString = (seconds < 10) ? '0' + seconds : seconds;
                displayText.textContent = `Soldier with ID ${soldierId} received ${response} points for raw score of ${minutes}:${secondsString}`;
            break;
            }
        case 6:
            {
                const minutesInput = document.getElementById('minutesInput');
                const secondsInput = document.getElementById('secondsInput');
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
                minutesInput.value = null;
                secondsInput.value = null;
                const secondsString = (seconds < 10) ? '0' + seconds : seconds;
                displayText.textContent = `Soldier with ID ${soldierId} received ${response} points for raw score of ${minutes}:${secondsString}`;
            break;
            }
        default: break;
    }
}

export async function editSoldierDataViewOnLoad(){
    await populateSoldiersByTestGroupIdController();
    eventInputController();
    document.getElementById('testGroupText').textContent = `Editing data in test group ${sessionStorage.getItem('selectedTestGroupId')}`;
    sessionStorage.setItem('view', '1');
    if (document.getElementById('soldierIdSelector').length === 0) document.getElementById('testGroupText').textContent = `No soldiers in test group ${sessionStorage.getItem('selectedTestGroupId')}`;
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
            mdlInput.className = 'field';
            targetElement.appendChild(mdlInput);
            targetElement.append("  [ lbs ]");
            break;
        case 2:
            const meters  = document.createElement('input');
            meters.type = 'number';
            meters.id = 'sptMetersInput';
            meters.className = 'field';
            const meterTenths = document.createElement('input');
            meterTenths.type = 'number';
            meterTenths.id = 'sptMeterTenthsInput';
            meterTenths.className = 'field';
            targetElement.appendChild(meters);
            targetElement.append(' [ meters ]');
            targetElement.appendChild(document.createElement('br'));
            targetElement.appendChild(meterTenths);
            targetElement.append(' [ meter tenths ]');
            break;
        case 3:
            const hrpInput = document.createElement('input');
            hrpInput.type = 'number';
            hrpInput.id = 'hrpInputField';
            hrpInput.className = 'field';
            targetElement.appendChild(hrpInput);
            targetElement.append("  [ repetitions ]");
            break;
        //Placed into blocks to circumvent redefinition warning
        case 4:
            {
            const minutes = document.createElement('input');
            minutes.type = 'number';
            minutes.id = 'minutesInput'
            minutes.className = 'field';
            const seconds = document.createElement('input');
            seconds.type = 'number';
            seconds.id = 'secondsInput';
            seconds.className = 'field';
            targetElement.appendChild(minutes);
            targetElement.append(' [ minutes ] ');
            targetElement.appendChild(document.createElement('br'));
            targetElement.appendChild(seconds);
            targetElement.append(" [ seconds ] ");
            }
            break;
        case 5:
            {
            const minutes = document.createElement('input');
            minutes.type = 'number';
            minutes.id = 'minutesInput'
            minutes.className = 'field';
            const seconds = document.createElement('input');
            seconds.type = 'number';
            seconds.id = 'secondsInput';
            seconds.className = 'field';
            targetElement.appendChild(minutes);
            targetElement.append(" [ minutes ]");
            targetElement.appendChild(document.createElement('br'));
            targetElement.appendChild(seconds);
            targetElement.append(" [ seconds ]");
            }
            break;
        case 6:
            const minutes = document.createElement('input');
            minutes.type = 'number';
            minutes.id = 'minutesInput'
            minutes.className = 'field';
            const seconds = document.createElement('input');
            seconds.type = 'number';
            seconds.id = 'secondsInput';
            seconds.className = 'field';
            targetElement.appendChild(minutes);
            targetElement.append(" [ minutes ]");
            targetElement.appendChild(document.createElement('br'));
            targetElement.appendChild(seconds);
            targetElement.append(" [ seconds ]");
            break;
        default: break;
    }
}

export function getHomePageViewController(){
    const host = getHost();
    API.getHomePageView(host);
}

export function displayDeleteSoldierSafeguard(){
    if (document.getElementById('soldierIdSelector').length === 0){
        document.getElementById('errorText').textContent = 'No soldiers in test group';
        return;
    }
    if (document.getElementById('safeguardText') !== null) return;
    const anchorPoint = document.getElementById('deleteSoldierButtonDiv');
    const break1 = document.createElement('br');
    const break2 = document.createElement('br');
    break1.className = "safeguard";
    break2.className = "safeguard";
    anchorPoint.appendChild(break1);
    const textSpan = document.createElement('p');
    textSpan.textContent = "Are you sure you want to delete the selected soldier?"
    textSpan.className = "safeguard";
    anchorPoint.appendChild(textSpan);
    anchorPoint.appendChild(break2);
    const noButton = document.createElement('button');
    const yesButton = document.createElement('button');
    noButton.className = 'safeguardButton button safeguard';
    yesButton.className = 'safeguardButton button safeguard';
    noButton.textContent = "NO";
    yesButton.textContent = "YES";
    noButton.addEventListener('click', noButtonClicked);
    yesButton.addEventListener('click', yesButtonClicked);
    anchorPoint.appendChild(noButton);
    anchorPoint.appendChild(yesButton);
    
}

export function showAboutViewController(){
    const host = getHost();
    API.getAboutView(host);
}


//==============    Component Functions   ================

function getHost(){
    return location.protocol + '//' + location.host;
}

async function populateSoldiersByTestGroupIdController(){
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
        element.textContent = soldier.id + `: ${soldier.lastName}, ${soldier.firstName}`
        element.value = soldier.id;
        soldierMenu.appendChild(element);
    });
}

async function yesButtonClicked(){
    const testGroupId = sessionStorage.getItem('selectedTestGroupId');
    const soldierId = document.getElementById('soldierIdSelector').value;
    const passcode = (sessionStorage.getItem('userPasscode') === null) ? undefined : sessionStorage.getItem('userPasscode');
    const host = getHost();
    try {
        await API.deleteSoldierById(testGroupId, soldierId, passcode, host);
    } catch (error) {
        console.log(error);
    }
    removeSafeguard();
    populateSoldiersByTestGroupIdController();
    document.getElementById('messageText').textContent = `Soldier with ID ${soldierId} deleted`;
}

function noButtonClicked(){
    removeSafeguard();
}

function removeSafeguard(){
    const anchorPoint = document.getElementById('deleteSoldierButtonDiv');
    const anchorChildren = document.getElementsByClassName('safeguard');
    while (anchorChildren.length > 0) anchorPoint.removeChild(anchorChildren[0]);
}


