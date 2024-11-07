import React, { useEffect, useState } from 'react';
import { fetchVideoById, getVideoStream, logUserActivity } from '../../api/api';
import { useParams } from 'react-router-dom';
import '../../static/css/VideoPlayer.css';

const VideoPlayer = () => {
    const { id } = useParams();
    const [video, setVideo] = useState(null);
    const [videoUrl, setVideoUrl] = useState(null);

    useEffect(() => {
        const loadVideo = async () => {
            try {
                const fetchedVideo = await fetchVideoById(id);
                setVideo(fetchedVideo);

                const videoBlob = await getVideoStream(id);
                const videoObjectUrl = URL.createObjectURL(videoBlob);
                setVideoUrl(videoObjectUrl);
            } catch (error) {
                console.error('Error loading video:', error);
            }
        };

        loadVideo();
    }, [id]);

    const handleViewStart = async () => {
        try {
            await logUserActivity({ videoId: id, activity: "VIEWED" });
        } catch (error) {
            console.error("Error logging video view:", error);
        }
    };

    if (!video || !videoUrl) return <div className="loading">Loading...</div>;

    return (
        <div className="video-player-container">
            <h2 className="video-title">{video.title}</h2>
            <div className="video-wrapper">
                <video
                    className="video-player"
                    controls
                    width="100%"
                    onPlay={handleViewStart}
                >
                    <source src={videoUrl} type="video/mp4" />
                    <source src={videoUrl} type="video/mkv" />
                    Your browser does not support the video tag.
                </video>
            </div>
            <p className="video-description">{video.description}</p>
        </div>
    );
};

export default VideoPlayer;