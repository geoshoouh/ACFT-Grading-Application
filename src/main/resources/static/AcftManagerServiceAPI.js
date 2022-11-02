
//const host = 'http://localhost:8080';

export async function createNewTestGroup(host){
    let response = await fetch(host + '/testGroup/new', {
        method: 'POST'
      }).then((response) => response).catch((error) => console.log(error));
    if (!response.ok) throw Error(`Response to testGroup/new was ${response.status}`);
    return response.json();
}

export async function createNewTestGroupWithPasscode(host, passcode){
  let response = await fetch(`${host}/testGroup/new/${passcode}`, {
        method: 'POST'
      }).then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to testGroup/new/${passcode} was ${response.status}`);
  return response.json();
}

export async function createNewSoldier(host, testGroupId, lastName, firstName, age, isMale){
  let response = await fetch(
    `${host}/testGroup/post/${testGroupId}/${lastName}/${firstName}/${age}/${isMale}`,
    {method: 'POST'})
    .then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to /testGroup/post/${testGroupId}/${lastName}/${firstName}/${age}/${isMale} was ${response.status}`);
  return response.json();
}

export async function getAllTestGroups(host){
  let response = await fetch(host + '/testGroup/get/all')
    .then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to /testGroup/get/all was ${response.status}`);
  return response.json();
}

export async function getEditSoldierDataView(host){
  location.replace(host + '/editSoldierData');
}

export async function getHomePageView(host){
  location.replace(host);
}

export async function getSoldiersByTestGroupId(host, testGroupId){
  if (typeof testGroupId != "number"){
    console.log(`getSoldiersByTestGroupId in ACFTManagerAPI expected Number; ${typeof soldierId} passed`);
    return;
  }
  let response = await fetch(
    `${host}/testGroup/getSoldiers/${testGroupId}`
  ).then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to /testGroup/getSoldiers/${testGroupId} was ${response.status}`);
  return response.json();
}

export async function getSoldierById(host, soldierId){
  let response = await fetch(
    `${host}/soldier/get/${soldierId}`
  ).then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to /get/${soldierId} was ${response.status}`);
  return response.json();
}

export async function getTestGroupById(host, testGroupId, passcode){
  let response = await fetch(
    `${host}/testGroup/get/${testGroupId}/${passcode}`
  ).then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to /testGroup/get/${testGroupId}/${passcode} was ${response.status}`);
  return response.json();
}

export async function updateSoldierScore(host, soldierId, eventId, rawScore){
  let response = await fetch(
    `${host}/soldier/updateScore/${soldierId}/${eventId-1}/${rawScore}`,
    {method: 'POST'})
    .then((response) => response).catch((error) => console.log(error));
    if (!response.ok) throw Error(`Response to /soldier/updateScore/${soldierId}/${eventId-1}/${rawScore} was ${response.status}`);
    return response.json();
}



