import * as API from './AcftManagerServiceAPI.js';
import { getHost } from './IndexViewController.js';

//==============    Exported Functions   ================

export async function visualizeTestDataViewOnLoad(){
    sessionStorage.setItem('view', '2');
    document.getElementById('testGroupBanner').textContent = `Test Group: ${sessionStorage.getItem('selectedTestGroupId')}`;
    dataTypeSelectionInterfaceController();
}

export async function dataTypeSelectionDidChange(){
    await dataTypeSelectionInterfaceController();
}


//==============    Component Functions   ================

async function dataTypeSelectionInterfaceController(){
    const host = getHost();
    const anchorPoint = document.getElementById('dataTypeSelectionDiv');
    const dataTypeSelection = document.getElementById('dataTypeSelector').value;
    switch (dataTypeSelection){
        case '1':
            const testGroupId = sessionStorage.getItem('selectedTestGroupId');
            const userPasscode = sessionStorage.getItem('userPasscode');
            //The scenario below triggers in the event that the location is pulled up before index is accessed in session
            if (testGroupId === null){
                document.getElementById('errorText').textContent = "Page not accessed properly; navigate to 'Home'";
                return;
            }
            let soldierPopulation = [];
            try{
                soldierPopulation = await API.getSoldiersByTestGroupId(testGroupId, userPasscode, host);
            } catch (error) {
                console.log(error);
                document.getElementById('errorText').textContent = "Server error";
                return;
            }
            const soldierIdSelector = document.createElement('select');
            soldierIdSelector.id = 'soldierIdSelector';
            soldierIdSelector.className = 'dropdown';
            soldierPopulation.forEach((soldier) => {
                let element = document.createElement('option');
                element.textContent = soldier.id + `: ${soldier.lastName}, ${soldier.firstName}`
                element.value = soldier.id;
                soldierIdSelector.appendChild(element);
            });
            anchorPoint.appendChild(soldierIdSelector);
            break;
        case '2':
            //Assuming there's no way case '2' can be selected without 'soldierIdSelector' existing
            anchorPoint.removeChild(document.getElementById('soldierIdSelector'));
            break;
        default: break;
    }

}
