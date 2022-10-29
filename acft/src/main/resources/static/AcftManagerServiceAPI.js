
export async function createNewTestGroup(){
    let groupId = await fetch('http://localhost:8080/testGroup/new', {
        method: 'POST'
      }).then((response) => response.json())
        .catch((error) => {
          console.log(error);
          return undefined;
        });
    return groupId;
}

export async function createNewTestGroupWithPasscode(passcode){
  let groupId = await fetch(`http://localhost:8080/testGroup/new/${passcode}`, {
        method: 'POST'
      }).then((response) => response.json())
        .catch((error) => {
          console.log(error);
          return undefined;
        });
    return groupId;
}

export async function createNewSoldier(testGroupId, lastName, firstName, age, isMale){
  let soldierId = await fetch(
    `http://localhost:8080/testGroup/post/${testGroupId}/${lastName}/${firstName}/${age}/${isMale}`,
    {method: 'POST'})
    .then((response) => response.json())
    .catch((error) => {
      console.log(error);
      return undefined;
    });
  return soldierId;
}

export async function getAllTestGroups(){
  let testGroupIds = await fetch('http://localhost:8080/testGroup/get/all')
    .then((response => response.json()))
    .catch((error) => {
      console.log(error);
      return undefined;
    });
  return testGroupIds;
}

export async function getEditSoldierDataView(){
  location.replace('http://localhost:8080/editSoldierData');
}

export async function getHomePageView(){
  location.replace('http://localhost:8080');
}

export async function getSoldiersByTestGroupId(testGroupId){
  if (typeof testGroupId != "number"){
    console.log(`getSoldiersByTestGroupId in ACFTManagerAPI expected Number; ${typeof soldierId} passed`);
    return;
  }
  let response = await fetch(
    `http://localhost:8080/testGroup/getSoldiers/${testGroupId}`
  ).then((response) => response.json())
  .catch((error) => {
    console.error('Error: ', error);
  });
  return response;
}

export async function getSoldierById(soldierId){
  let response = await fetch(
    `http://localhost:8080/soldier/get/${soldierId}`
  ).then((response) => response.json())
  .catch((error) => {
    console.error('Error: ' + error);
  });
  return response;
}

export async function getTestGroupById(testGroupId, passcode){
  let response = await fetch(
    `http://localhost:8080/testGroup/get/${testGroupId}/${passcode}`
  ).then((response) => response.json())
  .catch((error) => console.log(error));
  return response;
}

export async function updateSoldierScore(soldierId, eventId, rawScore){
  let convertedScore = await fetch(
    //eventId is 0-indexed in the backend, so it is decremented here
    `http://localhost:8080/soldier/updateScore/${soldierId}/${eventId-1}/${rawScore}`,
    {method: 'POST'})
    .then((response) => response.json())
    .catch((error) => {
      console.log(error);
      return undefined;
    });
  return convertedScore;
}



