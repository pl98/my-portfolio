// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
$(document).ready(function() {
    setTimeout(function() {
        $('.intro__tagline').fadeTo(700, 1);
    }, 1500);
    setTimeout(function() {
        $('.intro__contact').fadeTo(700, 1);
    }, 1700);
});

$(function() {
  const d = new Date();
  const hours = d.getHours();
  const night = hours >= 19 || hours <= 7; // between 7pm and 7am
  const body = document.querySelector('body');
  const toggle = document.getElementById('toggle');
  const input = document.getElementById('switch');

  if (night) {
    input.checked = true;
    body.classList.add('night');
  }

  toggle.addEventListener('click', function() {
    const isChecked = input.checked;
    if (isChecked) {
      body.classList.remove('night');
    } else {
      body.classList.add('night');
    }
  });

  const introHeight = document.querySelector('.intro').offsetHeight;
  const topButton = document.getElementById('top-button');
  const $topButton = $('#top-button');

  window.addEventListener(
    'scroll',
    function() {
      if (window.scrollY <= introHeight) {
        $topButton.fadeIn();
      } else {
        $topButton.fadeOut();
      }
    },
    false
  );

  topButton.addEventListener('click', function() {
    $('html, body').animate({ scrollTop: 0 }, 500);
  });

  const hand = document.querySelector('.emoji.wave-hand');

  function waveOnLoad() {
    hand.classList.add('wave');
    setTimeout(function() {
      hand.classList.remove('wave');
    }, 2000);
  }

  setTimeout(function() {
    waveOnLoad();
  }, 1000);

  hand.addEventListener('mouseover', function() {
    hand.classList.add('wave');
  });

  hand.addEventListener('mouseout', function() {
    hand.classList.remove('wave');
  });

  window.sr = ScrollReveal({
    reset: false,
    duration: 600,
    easing: 'cubic-bezier(.694,0,.335,1)',
    scale: 1,
    viewFactor: 0.3,
  });

  sr.reveal('.background');
  sr.reveal('.skills');
  sr.reveal('.experience', { viewFactor: 0.2 });
  sr.reveal('.featured-projects', { viewFactor: 0.1 });
  sr.reveal('.other-projects', { viewFactor: 0.05 });

  window.onload = getLoginStatus();
  window.onload = getComments();
});

function getComments() {
    const numComments = document.getElementById("comments-data").value;

    var queryString = "/data?numComments=" + numComments;

    fetch(queryString).then(response => response.json()).then((comments) => {
        const commentElement = document.getElementById("comment-container");
        comments.forEach((comment) => {
            commentElement.appendChild(createCommentElement(comment));
        })
    });
}

function createCommentElement(comment) {
  const commentElement = document.createElement('div');
  commentElement.className = 'comment';

  const nickName = document.createElement('div');
  nickName.className = 'nickname';
  nickName.innerText = "Anonymous";

  if (comment.nickname !== "") {
      nickName.innerText = comment.nickname;
  }

  commentElement.appendChild(nickName);

  const commentText = document.createElement('div');
  commentText.innerText = comment.title;

  commentElement.appendChild(commentText);

  return commentElement;
}

function refreshComments() {
    document.getElementById("comment-container").innerHTML = "";

    getComments();
}

function deleteComments() {
    const request = new Request('/delete-data', {method: 'POST'});

    fetch(request).then(() => {
        refreshComments();
    });
}

function getLoginStatus() {
    const button = document.createElement("button");
    button.type = "button";

    
    fetch("/login-status").then(response => response.json()).then((status) => {
        button.addEventListener('click', function() {
            window.location.href = status.url;
        });

        if (status.status) {
            document.getElementById("comment-form").style.display = "inline-block";

            const logInOut = document.getElementById("comment-form-title");
            logInOut.appendChild(document.createElement('br'));
            logInOut.appendChild(document.createElement('br'));
            
            button.innerText = "Log Out";

            logInOut.appendChild(button);
        }
        else {
            const commentForm = document.getElementById("comment-form-content");
            
            button.innerText = "Log In";
            button.id = "login-button";
            button.style.display = "inline-block";
            commentForm.appendChild(button);
        }
    });
}
